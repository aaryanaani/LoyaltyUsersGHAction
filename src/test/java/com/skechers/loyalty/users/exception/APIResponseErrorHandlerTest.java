package com.skechers.loyalty.users.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class APIResponseErrorHandlerTest {
	
	private static final String ABC_PQR_123 = "{\"abc\":{\"pqr\":\"123\"}}";

	private static final String ERROR_OCCURED = "error occured";

	private static final String ERRORS_MESSAGE_ERROR_OCCURED = "{\"errors\":{\"message\":\"error occured\"}}";
	
	public static final String DEFAULT_ERROR_MSG = "Unknown error. Please try again later.";
	
	@InjectMocks
	APIResponseErrorHandler errorHandler;

	@Mock
	MockClientHttpResponse httpResponse;
	
	@Test
	void handleErrorWithClientExceptionAndConstant() throws IOException {
		byte[] body = ERRORS_MESSAGE_ERROR_OCCURED.getBytes();
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(body, HttpStatus.BAD_REQUEST);
		
		Exception exception = assertThrows(HttpClientErrorException.class, () -> {
			errorHandler.handleError(clientHttpResponse);
		});

		String expectedMessage = ERROR_OCCURED;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void handleErrorWithClientException() throws IOException {
		byte[] body = "{\"errors\":{\"Message\":\"error occured\"}}".getBytes();
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(body, HttpStatus.BAD_REQUEST);
		
		Exception exception = assertThrows(HttpClientErrorException.class, () -> {
			errorHandler.handleError(clientHttpResponse);
		});

		String expectedMessage = ERROR_OCCURED;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void handleErrorWithClientExceptionAndDefaultMsg() throws IOException {
		byte[] body = ABC_PQR_123.getBytes();
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(body, HttpStatus.BAD_REQUEST);
		
		Exception exception = assertThrows(HttpClientErrorException.class, () -> {
			errorHandler.handleError(clientHttpResponse);
		});

		String expectedMessage = DEFAULT_ERROR_MSG;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void handleErrorWithServiceException() throws IOException {
		byte[] body = ERRORS_MESSAGE_ERROR_OCCURED.getBytes();
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(body, HttpStatus.INTERNAL_SERVER_ERROR);
		
		Exception exception = assertThrows(HttpServerErrorException.class, () -> {
			errorHandler.handleError(clientHttpResponse);
		});

		String expectedMessage = ERROR_OCCURED;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void handleErrorWithServiceExceptionAndDefaultErrorMsg() throws IOException {
		byte[] body = ABC_PQR_123.getBytes();
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(body, HttpStatus.INTERNAL_SERVER_ERROR);
		
		Exception exception = assertThrows(HttpServerErrorException.class, () -> {
			errorHandler.handleError(clientHttpResponse);
		});

		String expectedMessage = DEFAULT_ERROR_MSG;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void handleErrorWithUnknownExceptions() throws IOException {
		byte[] body = ERRORS_MESSAGE_ERROR_OCCURED.getBytes();
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(body, HttpStatus.CHECKPOINT);
		
		Exception exception = assertThrows(UnknownHttpStatusCodeException.class, () -> {
			errorHandler.handleError(clientHttpResponse);
		});

		String expectedMessage = "Unknown Error";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void hasErrorWithSuccessfulResponse() throws IOException {
		byte[] body = ABC_PQR_123.getBytes();
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(body, HttpStatus.OK);
		
		Boolean fromMethod = errorHandler.hasError(clientHttpResponse);
		
		assertTrue(!fromMethod);
	}
	
	@Test
	void hasErrorWithUnsuccessfulResponse() throws IOException {
		byte[] body = ABC_PQR_123.getBytes();
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(body, HttpStatus.INTERNAL_SERVER_ERROR);
		
		Boolean fromMethod = errorHandler.hasError(clientHttpResponse);
		
		assertTrue(fromMethod);
	}
	
}
