package com.ps.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ps.constant.IExceptionConstants;
import com.ps.constant.IResponseConstants;
import com.ps.dto.request.TaskCreationRequest;
import com.ps.dto.request.TaskRequest;
import com.ps.dto.response.CommentResponse;
import com.ps.dto.response.SubTaskResponse;
import com.ps.dto.response.TaskListResponse;
import com.ps.dto.response.TaskResponse;
import com.ps.entity.Comment;
import com.ps.entity.Project;
import com.ps.entity.SubTask;
import com.ps.entity.Task;
import com.ps.entity.User;
import com.ps.enu.TaskStatus;
import com.ps.exception.TaskException;
import com.ps.helper.TaskMappingHelper;
import com.ps.repo.CommentRepository;
import com.ps.repo.ProjectRepository;
import com.ps.repo.SubTaskRepository;
import com.ps.repo.TaskRepository;
import com.ps.repo.UserRepository;
import com.ps.service.ITaskService;
import com.ps.util.CommonUtil;

import jakarta.transaction.Transactional;

@Service
public class TaskServiceImpl implements ITaskService {
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private SubTaskRepository subTaskRepository;
	
	@Autowired
	private CommentRepository commentRepository;

	@Override
	public TaskResponse createTask(TaskCreationRequest request) {
		Long assignedToId = request.getAssignedToId();
		if (!userRepository.existsById(assignedToId)) {
			throw new TaskException(IExceptionConstants.USER_NOT_FOUND_FOR_ASSIGN_ID, HttpStatus.BAD_REQUEST);
		}
		Long projectId = request.getProjectId();
		if (!projectRepository.existsById(projectId)) {
			throw new TaskException(IExceptionConstants.PROJECT_NOT_FOUND, HttpStatus.BAD_REQUEST);
		}
		User assigendTo = userRepository.findById(assignedToId).get();
		User createdBy = userRepository.findByEmail(CommonUtil.getUserEmail()).get();
		Project project = projectRepository.findById(projectId).get();
		
		Task task = TaskMappingHelper.toTask(request);
		task.setAssignedTo(assigendTo);
		task.setCreatedBy(createdBy);
		task.setProject(project);
		task = taskRepository.save(task);
		if (task.getId() == null) {
			throw new TaskException(IExceptionConstants.TASK_CREATION_FAIL);
		}
		return TaskMappingHelper.toTaskResponse(task);
	}

	@Override
	public TaskListResponse filterTasks(TaskRequest request) {
		Long userId = null;
		if (request.getAssignedTo() != null && request.getAssignedTo().equals("me")) {
			userId = userRepository.findByEmail(CommonUtil.getUserEmail()).get().getId();
		}
		Page<Task> taskPages = taskRepository.searchTasks(request.getProjectId(), userId, 
				request.getStatus(), request.getPriority(), request.getDueDate(), request.getSearch(),
				request.getPageable());
		return TaskMappingHelper.toTaskListResponse(taskPages);
	}

	@Override
	public TaskResponse getTask(Long id) {
		Optional<Task> optional = taskRepository.findById(id);
		if (optional.isEmpty()) {
			throw new TaskException(IExceptionConstants.TASK_NOT_FOUND, HttpStatus.BAD_REQUEST);
		}
		Task task = optional.get();
		List<SubTask> subTasks = subTaskRepository.findByTask(task);
		List<Comment> comments = commentRepository.findByTask(task);
		List<SubTaskResponse> subTaskResponseList = subTasks.stream()
				.map(TaskMappingHelper::toSubTaskResponse)
				.collect(Collectors.toList());
		List<CommentResponse> commentResponseList = comments.stream()
				.map(TaskMappingHelper::toCommentResponse)
				.collect(Collectors.toList());
		
		TaskResponse taskResponse = TaskMappingHelper.toTaskResponse(task);
		taskResponse.setSubtasks(subTaskResponseList);
		taskResponse.setComments(commentResponseList);
		return taskResponse;
	}

	@Override
	public TaskResponse editTask(TaskCreationRequest request, Long id) {
		Optional<Task> optional = taskRepository.findById(id);
		if (optional.isEmpty()) {
			throw new TaskException(IExceptionConstants.TASK_NOT_FOUND, HttpStatus.BAD_REQUEST);
		}
		Long assignedToId = request.getAssignedToId();
		if (!userRepository.existsById(assignedToId)) {
			throw new TaskException(IExceptionConstants.USER_NOT_FOUND_FOR_ASSIGN_ID, HttpStatus.BAD_REQUEST);
		}
		User assigendTo = userRepository.findById(assignedToId).get();
		
		Task task = optional.get();
		task.setAssignedTo(assigendTo);
		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		task.setStatus(request.getStatus());
		task.setPriority(request.getPriority());
		task.setDueDate(request.getDueDate());
		task = taskRepository.save(task);
		return TaskMappingHelper.toTaskResponse(task);
	}

	@Override
	@Transactional
	public String changeTaskStatus(TaskStatus newStatus, Long id) {
		if (!taskRepository.existsById(id)) {
			throw new TaskException(IExceptionConstants.TASK_NOT_FOUND, HttpStatus.BAD_REQUEST);
		}
		int updateStatus = taskRepository.updateStatus(newStatus, id);
		if (updateStatus == 1) {
			return IResponseConstants.STATUS_CHANGE_SUCCESS;
		} else {
			throw new TaskException(IExceptionConstants.TASK_STATUS_CHANGE_FAILED);
		}
	}

	@Override
	public CommentResponse publishComment(Long id, String content) {
		Optional<Task> optional = taskRepository.findById(id);
		if (optional.isEmpty()) {
			throw new TaskException(IExceptionConstants.TASK_NOT_FOUND, HttpStatus.BAD_REQUEST);
		}
		Comment comment = TaskMappingHelper.toComment(content);
		comment.setTask(optional.get());
		comment.setUser(userRepository.findByEmail(CommonUtil.getUserEmail()).get());
		comment = commentRepository.save(comment);
		if (comment.getId() == null) {
			throw new TaskException(IExceptionConstants.COMMENT_FAIL);
		}
		return TaskMappingHelper.toCommentResponse(comment);
	}

	@Override
	public SubTaskResponse createSubTask(Long id, String title) {
		Optional<Task> optional = taskRepository.findById(id);
		if (optional.isEmpty()) {
			throw new TaskException(IExceptionConstants.TASK_NOT_FOUND, HttpStatus.BAD_REQUEST);
		}
		SubTask subTask = TaskMappingHelper.toSubTask(title);
		subTask.setTask(optional.get());
		subTask = subTaskRepository.save(subTask);
		if (subTask.getId() == null) {
			throw new TaskException(IExceptionConstants.SUBTASK_CREATION_FAIL);
		}
		return TaskMappingHelper.toSubTaskResponse(subTask);
	}

	@Override
	@Transactional
	public String changeSubTaskStatus(Long id, boolean isCompleted) {
		if (!subTaskRepository.existsById(id)) {
			throw new TaskException(IExceptionConstants.SUBTASK_NOT_FOUND, HttpStatus.BAD_REQUEST);
		}
		int updated = subTaskRepository.updateSubTaskCompltedStatus(isCompleted, id);
		if (updated == 1) {
			return IResponseConstants.SUBTASK_COMPLETED_UDPATE;
		}
		throw new TaskException(IExceptionConstants.SUBTASK_COMPLETED_CHANGE_FAILED);
	}
}
