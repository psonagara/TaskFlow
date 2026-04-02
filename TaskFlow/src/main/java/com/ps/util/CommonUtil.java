package com.ps.util;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ps.dto.response.ApiResponse;

public interface CommonUtil {
	
	public static ResponseEntity<ApiResponse<?>> prepareResponseWithMessage(String message, HttpStatus status) {
		return prepareApiResponse(message, status, null);
	}

	public static ResponseEntity<ApiResponse<?>> prepareResponseWithContent(Object body, HttpStatus status) {
		return prepareApiResponse(null, status, body);
	}

	public static ResponseEntity<ApiResponse<?>> prepareApiResponse(String message, HttpStatus status, Object body) {
	    ApiResponse<Object> apiResponse = ApiResponse.builder()
				.message(message)
				.content(body)
				.status(status.toString())
				.statusCode(status.value())
				.timestamp(LocalDateTime.now())
				.build();
		return new ResponseEntity<ApiResponse<?>>(apiResponse, status);
	}
	
	public static String getUserEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
