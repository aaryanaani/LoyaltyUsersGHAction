package com.skechers.loyalty.users.exception;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.util.IOUtils;

public class APIResponseErrorHandler implements ResponseErrorHandler {

	private static final String ERROR_TAG = "errors";
	private static final String ERROR_MESSAGE = "message";
	public static final String DEFAULT_ERROR_MSG = "Unknown error. Please try again later.";


	@Override
	public void handleError(ClientHttpResponse clienthttpresponse) throws IOException {

		switch (clienthttpresponse.getStatusCode().series()) {
		case CLIENT_ERROR:
			processClientExceptions(clienthttpresponse);
			break;
		case SERVER_ERROR:
			processServerExceptions(clienthttpresponse);
			break;
		default:
			processUnknownExceptions(clienthttpresponse);
		}
	}

	@Override
	public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException {

		return clienthttpresponse.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL ? Boolean.TRUE
				: Boolean.FALSE;
	}

	private void processClientExceptions(ClientHttpResponse clienthttpresponse) throws IOException {
		String text = IOUtils.toString(clienthttpresponse.getBody());
		JsonNode root = new ObjectMapper().readTree(text);
		String errorMessage = null;
		if (root.has(ERROR_TAG) && (root.get(ERROR_TAG).has("Message") || root.get(ERROR_TAG).has(ERROR_MESSAGE))) {
			errorMessage = root.get(ERROR_TAG).has(ERROR_MESSAGE) ? root.get(ERROR_TAG).get(ERROR_MESSAGE).asText()
					: root.get(ERROR_TAG).get("Message").asText();
		} else {
			errorMessage = DEFAULT_ERROR_MSG;
		}

		throw new HttpClientErrorException(clienthttpresponse.getStatusCode(), errorMessage);
	}

	private void processServerExceptions(ClientHttpResponse clienthttpresponse) throws IOException {
		String text = IOUtils.toString(clienthttpresponse.getBody());
		JsonNode root = new ObjectMapper().readTree(text);
		String errorMessage = DEFAULT_ERROR_MSG;
		if (root.has(ERROR_TAG) && root.get(ERROR_TAG).has(ERROR_MESSAGE)) {
			errorMessage = root.get(ERROR_TAG).get(ERROR_MESSAGE).asText();
		}
		throw new HttpServerErrorException(clienthttpresponse.getStatusCode(), errorMessage);
	}

	private void processUnknownExceptions(ClientHttpResponse clienthttpresponse) throws IOException {
		throw new UnknownHttpStatusCodeException(clienthttpresponse.getRawStatusCode(), "Unknown Error",
				clienthttpresponse.getHeaders(), getResponseBody(clienthttpresponse), getCharset(clienthttpresponse));
	}

	protected Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		return (contentType != null ? contentType.getCharset() : null);
	}

	protected byte[] getResponseBody(ClientHttpResponse response) {
		try {
			return FileCopyUtils.copyToByteArray(response.getBody());
		} catch (IOException ex) {
			// ignore
		}
		return new byte[0];
	}
}