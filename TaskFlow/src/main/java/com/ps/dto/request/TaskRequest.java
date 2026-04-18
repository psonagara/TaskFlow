package com.ps.dto.request;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import com.ps.enu.TaskPriority;
import com.ps.enu.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

	private Long projectId;
	private String assignedTo;
	private TaskStatus status;
	private TaskPriority priority;
	private LocalDate dueDate;
	private String search;
	private Pageable pageable;
}
