package com.ps.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ps.dto.response.ErrorResponse;
import com.ps.exception.AuthException;
import com.ps.exception.ProjectException;
import com.ps.exception.TaskException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(AuthException.class)
	public ResponseEntity<ErrorResponse> handleAuthException(AuthException authException) {
		HttpStatus status = authException.getStatus() != null ? authException.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		ErrorResponse response = new ErrorResponse();
		response.setMessage(authException.getMessage());
		response.setStatus(status);
		return new ResponseEntity<>(response, status);
	}
	
	@ExceptionHandler(ProjectException.class)
	public ResponseEntity<ErrorResponse> handleProjectException(ProjectException projectException) {
		HttpStatus status = projectException.getStatus() != null ? projectException.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		ErrorResponse response = new ErrorResponse();
		response.setMessage(projectException.getMessage());
		response.setStatus(status);
		return new ResponseEntity<>(response, status);
	}
	
	@ExceptionHandler(TaskException.class)
	public ResponseEntity<ErrorResponse> handleTaskException(TaskException taskException) {
		HttpStatus status = taskException.getStatus() != null ? taskException.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
		ErrorResponse response = new ErrorResponse();
		response.setMessage(taskException.getMessage());
		response.setStatus(status);
		return new ResponseEntity<>(response, status);
	}
}
