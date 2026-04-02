package com.ps.service;

import com.ps.dto.request.TaskCreationRequest;
import com.ps.dto.request.TaskRequest;
import com.ps.dto.response.CommentResponse;
import com.ps.dto.response.SubTaskResponse;
import com.ps.dto.response.TaskListResponse;
import com.ps.dto.response.TaskResponse;
import com.ps.enu.TaskStatus;

public interface ITaskService {

	public TaskResponse createTask(TaskCreationRequest request);
	public TaskListResponse filterTasks(TaskRequest request);
	public TaskResponse getTask(Long id);
	public TaskResponse editTask(TaskCreationRequest request, Long id);
	public String changeTaskStatus(TaskStatus newStatus, Long id);
	public CommentResponse publishComment(Long id, String content);
	public SubTaskResponse createSubTask(Long id, String title);
	public String changeSubTaskStatus(Long id, boolean isCompleted);
}
