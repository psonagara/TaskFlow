package com.ps.dto.request;

import java.time.LocalDate;

import com.ps.enu.TaskPriority;
import com.ps.enu.TaskStatus;

import lombok.Data;

@Data
public class TaskCreationRequest {
	
	private String title;
	private String description;
	private TaskPriority priority;
	private TaskStatus status;
	private Long assignedToId;
	private LocalDate dueDate;
	private Long projectId;
}
