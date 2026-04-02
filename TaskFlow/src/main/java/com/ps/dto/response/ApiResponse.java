package com.ps.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

	private T content;
	private String message;
	private String status;
	private int statusCode;
	private LocalDateTime timestamp;
}
