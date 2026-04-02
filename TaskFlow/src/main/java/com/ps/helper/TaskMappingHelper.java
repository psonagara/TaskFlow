package com.ps.helper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.ps.dto.request.TaskCreationRequest;
import com.ps.dto.response.CommentResponse;
import com.ps.dto.response.ProjectMemberResponse;
import com.ps.dto.response.ProjectResponse;
import com.ps.dto.response.SubTaskResponse;
import com.ps.dto.response.TaskListResponse;
import com.ps.dto.response.TaskResponse;
import com.ps.entity.Comment;
import com.ps.entity.SubTask;
import com.ps.entity.Task;

public interface TaskMappingHelper {

	public static Task toTask(TaskCreationRequest request) {
		Task task = new Task();
		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		task.setStatus(request.getStatus());
		task.setPriority(request.getPriority());
		task.setDueDate(request.getDueDate());
		return task;
	}
	
	public static TaskResponse toTaskResponse(Task task) {
		TaskResponse response = new TaskResponse();
		response.setId(task.getId());
		response.setTitle(task.getTitle());
		response.setDescription(task.getDescription());
		response.setStatus(task.getStatus());
		response.setPriority(task.getPriority());
		response.setDueDate(task.getDueDate());
		response.setProjectId(task.getProject().getId());
		
		ProjectResponse projectResponse = new ProjectResponse();
		projectResponse.setId(task.getProject().getId());
		projectResponse.setName(task.getProject().getName());
		response.setProject(projectResponse);

		ProjectMemberResponse projectMemberResponse = new ProjectMemberResponse();
		projectMemberResponse.setId(task.getAssignedTo().getId());
		projectMemberResponse.setName(task.getAssignedTo().getName());
		projectMemberResponse.setEmail(task.getAssignedTo().getEmail());
		response.setAssignedTo(projectMemberResponse);
		
		response.setCreatedAt(task.getCreatedAt());
		response.setUpdatedAt(task.getUpdatedAt());
		return response;
	}
	
	public static TaskListResponse toTaskListResponse(Page<Task> taskPages) {
		List<TaskResponse> taskList = taskPages
				.getContent()
				.stream()
				.map(TaskMappingHelper::toTaskResponse)
				.collect(Collectors.toList());
		TaskListResponse response = new TaskListResponse();
		response.setTasksList(taskList);
		response.setTotalElements(taskPages.getTotalElements());
		response.setNumber(taskPages.getNumber());
		response.setSize(taskPages.getSize());
		response.setTotalPages(taskPages.getTotalPages());
		return response;
	}
	
	public static SubTaskResponse toSubTaskResponse(SubTask subTask) {
		SubTaskResponse response = new SubTaskResponse();
		response.setCompleted(subTask.isCompleted());
		response.setId(subTask.getId());
		response.setTitle(subTask.getTitle());
		return response;
	}

	public static CommentResponse toCommentResponse(Comment comment) {
		CommentResponse response = new CommentResponse();
		response.setContent(comment.getContent());
		response.setCreatedAt(comment.getCreatedAt());
		response.setId(comment.getId());
		response.setUser(ProjectMappingHelper.toProjectMemberResponse(comment.getUser()));
		return response;
	}
	
	public static Comment toComment(String content) {
		Comment comment = new Comment();
		comment.setContent(content);
		return comment;
	}
	
	public static SubTask toSubTask(String content) {
		SubTask subTask = new SubTask();
		subTask.setTitle(content);
		return subTask;
	}
}
