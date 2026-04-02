package com.ps.rest;

import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constant.IMappingConstants;
import com.ps.constant.IResponseConstants;
import com.ps.dto.request.TaskCreationRequest;
import com.ps.dto.request.TaskRequest;
import com.ps.dto.response.CommentResponse;
import com.ps.dto.response.SubTaskResponse;
import com.ps.dto.response.TaskListResponse;
import com.ps.dto.response.TaskResponse;
import com.ps.enu.TaskPriority;
import com.ps.enu.TaskStatus;
import com.ps.service.ITaskService;
import com.ps.util.CommonUtil;

@RestController
@RequestMapping(IMappingConstants.TASK_API_PATH)
public class TaskRestController {
	
	private static final Logger LOG = LoggerFactory.getLogger(TaskRestController.class);
	
	@Autowired
	private ITaskService taskService;

	@PostMapping
	public ResponseEntity<?> createTask(@RequestBody TaskCreationRequest request) {
		LOG.debug("Enter in TaskRestController.createTask, " + request);
		TaskResponse response = taskService.createTask(request);
		return CommonUtil.prepareApiResponse(IResponseConstants.TASK_CREATED_SUCESS, HttpStatus.CREATED, response);
	}
	
	@GetMapping
	public ResponseEntity<?> filterTasks(@RequestParam(name = "projectId", required = false) Long projectId,
			@RequestParam(name = "assignedTo", required = false) String assignedTo,
			@RequestParam(name = "status", required = false) TaskStatus status,
			@RequestParam(name = "priority", required = false) TaskPriority priority,
			@RequestParam(name = "dueDate", required = false) LocalDate dueDate,
			@RequestParam(name = "search", required = false) String search,
			@PageableDefault(page = 0, size = 10) Pageable pageable
			) {
		LOG.debug("Enter in TaskRestController.filterTasks");
		
		TaskRequest request = new TaskRequest();
		request.setProjectId(projectId);
		request.setAssignedTo(assignedTo);
		request.setStatus(status);
		request.setPriority(priority);
		request.setDueDate(dueDate);
		request.setSearch(search);
		request.setPageable(pageable);
		
		LOG.debug("TaskRequest: " + request);
		
		TaskListResponse response = taskService.filterTasks(request);
		return CommonUtil.prepareResponseWithContent(response, HttpStatus.OK);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> getTask(@PathVariable("id") Long id) {
		LOG.debug("Enter in TaskRestController.getTask, " + id);
		TaskResponse response = taskService.getTask(id);
		return CommonUtil.prepareResponseWithContent(response, HttpStatus.OK);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> editTask(@PathVariable("id") Long id, @RequestBody TaskCreationRequest request) {
		LOG.debug("Enter in TaskRestController.editTask, " + id + ", " + request);
		TaskResponse response = taskService.editTask(request, id);
		return CommonUtil.prepareResponseWithContent(response, HttpStatus.OK);
	}

	@PutMapping("{id}/status")
	public ResponseEntity<?> changeTaskStatus(@PathVariable("id") Long id, @RequestBody Map<String, TaskStatus> request) {
		LOG.debug("Enter in TaskRestController.changeTaskStatus, " + id + ", " + request);
		String message = taskService.changeTaskStatus(request.get("status"), id);
		return CommonUtil.prepareResponseWithMessage(message, HttpStatus.OK);
	}
	
	@PostMapping("{id}/comments")
	public ResponseEntity<?> publishComment(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
		LOG.debug("Enter in TaskRestController.publishComment, " + id + ", " + request);
		CommentResponse response = taskService.publishComment(id, request.get("content"));
		return CommonUtil.prepareApiResponse(IResponseConstants.COMMENT_SUCCESS, HttpStatus.CREATED, response);
	}
	
	@PostMapping("{id}/subtasks")
	public ResponseEntity<?> createSubTask(@PathVariable("id") Long id, @RequestBody Map<String, String> request) {
		LOG.debug("Enter in TaskRestController.createSubTask, " + id + ", " + request);
		SubTaskResponse response = taskService.createSubTask(id, request.get("title"));
		return CommonUtil.prepareApiResponse(IResponseConstants.SUBTASK_CREATED_SUCCESS, HttpStatus.CREATED, response);
	}
	
	@PutMapping("subtask/{id}/{isCompleted}")
	public ResponseEntity<?> changeSubTaskStatus(@PathVariable("id") Long id, @PathVariable("isCompleted") boolean isCompleted) {
		LOG.debug("Enter in TaskRestController.changeSubTaskStatus, " + id + ", " + isCompleted);
		String message = taskService.changeSubTaskStatus(id, isCompleted);
		return CommonUtil.prepareResponseWithMessage(message, HttpStatus.OK);
	}
	
}
