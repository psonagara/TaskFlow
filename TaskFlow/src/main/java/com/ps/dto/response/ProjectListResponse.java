package com.ps.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class ProjectListResponse {

	private List<ProjectResponse> projects;
	private long totalElements;
	private int totalPages;
	private int number;
	
}
