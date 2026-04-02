package com.ps.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProjectResponse {

	private Long id;
	
	private String name;
	
	private String description;
	
	private LocalDateTime creationAt;

	private LocalDateTime updateAt;
}
