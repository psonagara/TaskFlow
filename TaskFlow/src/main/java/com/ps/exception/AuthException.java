package com.ps.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class AuthException extends RuntimeException {

	private static final long serialVersionUID = 5119279023577863048L;
	
	@Getter
	private HttpStatus status;

	public AuthException() {
	}

	public AuthException(String msg) {
		super(msg);
	}
	
	public AuthException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public AuthException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}
	
}
