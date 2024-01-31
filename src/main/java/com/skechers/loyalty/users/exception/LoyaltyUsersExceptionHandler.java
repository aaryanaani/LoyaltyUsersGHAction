package com.skechers.loyalty.users.exception;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.extern.slf4j.Slf4j;

@EnableWebMvc
@ControllerAdvice
@Slf4j
public class LoyaltyUsersExceptionHandler {
	

	@ExceptionHandler({ HttpClientErrorException.class, HttpServerErrorException.class })
	ResponseEntity<ErrorData> handleConflict(final HttpClientErrorException ex) {
		ErrorData errorData = new ErrorData();
		errorData.setTimestamp(LocalDateTime.now());
		errorData.setError(ex.getStatusText());
		errorData.setStatus(ex.getStatusCode().value());
		return new ResponseEntity<>(errorData, ex.getStatusCode());
	}
	
	@SuppressWarnings("unchecked")
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	public ResponseEntity<Object> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
		e.printStackTrace();
		SessionMErrorResponse error = new SessionMErrorResponse();
		error.setError(HttpStatus.BAD_REQUEST.name());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setTimestamp(LocalDateTime.now());
		JSONObject message = new JSONObject();
		message.put("error", "Invalid request");
		error.setMessage(message);
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ RuntimeException.class })
	ResponseEntity<ErrorData> handleRuntimeException(final RuntimeException ex) {
		ex.printStackTrace();
		ErrorData errorData = new ErrorData();
		errorData.setTimestamp(LocalDateTime.now());
		errorData.setError(ex.getMessage());
		errorData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(errorData, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ CustomException.class })
	public ResponseEntity<Object> customException(final CustomException e) {
		e.printStackTrace();
		HttpStatus httpStatus = e.getHttpStatus() != null ? e.getHttpStatus() : HttpStatus.BAD_REQUEST;
		ErrorData error = new ErrorData();
		error.setError(e.getMessage());
		error.setStatus(httpStatus.value());
		error.setTimestamp(LocalDateTime.now());
		return new ResponseEntity<>(error, httpStatus);
	}	

	@ExceptionHandler({ WebClientResponseException.class })
	public ResponseEntity<Object> handleWebClientResponseException(final WebClientResponseException e) {
		log.error(e.getMessage());
		HttpStatus httpStatus = e.getStatusCode();
		SessionMErrorResponse error = new SessionMErrorResponse();
		error.setError(httpStatus.name());
		error.setStatus(httpStatus.value());
		error.setTimestamp(LocalDateTime.now());
		error.setMessage(getExceptionResponse(e));
		return new ResponseEntity<>(error, httpStatus);
	}
	
	private JSONObject getExceptionResponse(WebClientResponseException e) {
		try {
			JSONParser parser = new JSONParser();  
			return (JSONObject) parser.parse(e.getResponseBodyAsString());
		} catch (Exception ex) {
			return new JSONObject();
		}
	}
}
