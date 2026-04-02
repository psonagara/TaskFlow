package com.ps.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class TaskListResponse {

	private List<TaskResponse> tasksList;
	private Long totalElements;
	private Integer totalPages;
	private Integer number;
	private Integer size;
}
