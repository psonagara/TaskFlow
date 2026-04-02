package com.ps.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class TaskException extends RuntimeException {

	private static final long serialVersionUID = -2762350754578113683L;

	@Getter
	private HttpStatus status;

	public TaskException() {
	}

	public TaskException(String msg) {
		super(msg);
	}
	
	public TaskException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public TaskException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}
}
