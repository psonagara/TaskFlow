package com.ps.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class ProjectException extends RuntimeException {

	private static final long serialVersionUID = -1770995704854207280L;

	@Getter
	private HttpStatus status;

	public ProjectException() {
	}

	public ProjectException(String msg) {
		super(msg);
	}
	
	public ProjectException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ProjectException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}
}
