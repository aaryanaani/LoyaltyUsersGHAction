package com.skechers.loyalty.users.exception;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SessionMErrorResponse {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	private LocalDateTime timestamp;
	private int status;
	private String error;
	private JSONObject message;

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public JSONObject getMessage() {
		return message;
	}

	public void setMessage(JSONObject message) {
		this.message = message;
	}

}
