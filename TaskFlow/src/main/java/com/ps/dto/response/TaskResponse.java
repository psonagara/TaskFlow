package com.ps.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.ps.enu.TaskPriority;
import com.ps.enu.TaskStatus;

import lombok.Data;

@Data
public class TaskResponse {

	private Long id;
	private String title;
	private String description;
	private TaskStatus status;
	private TaskPriority priority;
	private LocalDate dueDate;
	private Long projectId;
	private ProjectResponse project;
	private ProjectMemberResponse assignedTo;
	private List<SubTaskResponse> subtasks;
	private List<CommentResponse> comments;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

