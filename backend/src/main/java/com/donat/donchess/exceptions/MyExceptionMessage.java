package com.donat.donchess.exceptions;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public class MyExceptionMessage {

	private HttpStatus status;

	private String message;

	private LocalDateTime timeStamp;

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
}
