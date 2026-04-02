package com.ps.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentResponse {

	private Long id;
	private String content;
	private LocalDateTime createdAt;
	private ProjectMemberResponse user;
}
