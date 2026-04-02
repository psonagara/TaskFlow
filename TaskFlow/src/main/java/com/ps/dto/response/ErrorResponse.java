package com.ps.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorResponse {

	private String message;
	private HttpStatus status;
	private LocalDateTime timestamp = LocalDateTime.now();
}
