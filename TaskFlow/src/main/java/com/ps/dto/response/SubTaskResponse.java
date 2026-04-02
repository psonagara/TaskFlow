package com.ps.dto.response;

import lombok.Data;

@Data
public class SubTaskResponse {

	private Long id;
	private String title;
	private Boolean completed;
}
