package com.ps.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProjectCreationResponse {

	private Long id;
	private String name;
	private String description;
	private LocalDateTime createdAt;
}
