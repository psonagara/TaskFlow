package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.ps.constant.IExceptionConstants;
import com.ps.constant.IResponseConstants;
import com.ps.dto.request.TaskCreationRequest;
import com.ps.dto.request.TaskRequest;
import com.ps.dto.response.CommentResponse;
import com.ps.dto.response.SubTaskResponse;
import com.ps.dto.response.TaskListResponse;
import com.ps.dto.response.TaskResponse;
import com.ps.entity.Comment;
import com.ps.entity.SubTask;
import com.ps.entity.Task;
import com.ps.entity.User;
import com.ps.enu.TaskStatus;
import com.ps.exception.TaskException;
import com.ps.repo.CommentRepository;
import com.ps.repo.ProjectRepository;
import com.ps.repo.SubTaskRepository;
import com.ps.repo.TaskRepository;
import com.ps.repo.UserRepository;
import com.ps.util.CommonUtil;
import com.ps.util.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
	
	@InjectMocks
	private TaskServiceImpl taskService;
	
	@Mock
	private TaskRepository taskRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private ProjectRepository projectRepository;
	
	@Mock
	private SubTaskRepository subTaskRepository;
	
	@Mock
	private CommentRepository commentRepository;


	@Test
	void testCreateTask() {
		TaskCreationRequest request = TestDataUtil.getTaskCreationRequest();
		Task task = TestDataUtil.getTask();
		User user = TestDataUtil.getUser();
		Long assignedToId = request.getAssignedToId();
		Long projectId = request.getProjectId();
		
		when(userRepository.existsById(assignedToId)).thenReturn(true);
		when(projectRepository.existsById(projectId)).thenReturn(true);
		when(userRepository.findById(assignedToId)).thenReturn(Optional.of(user));
		MockedStatic<CommonUtil> mockStatic = Mockito.mockStatic(CommonUtil.class);
		mockStatic.when(() -> CommonUtil.getUserEmail()).thenReturn(TestDataUtil.getUserEmail());
		when(userRepository.findByEmail(CommonUtil.getUserEmail())).thenReturn(Optional.of(user));
		when(projectRepository.findById(projectId)).thenReturn(Optional.of(TestDataUtil.getProject()));
		when(taskRepository.save((Task) any())).thenReturn(task);
		
		// success scenario
		TaskResponse response = taskService.createTask(request);
		assertEquals(task.getId(), response.getId());
		assertEquals(task.getTitle(), response.getTitle());
		assertEquals(task.getDescription(), response.getDescription());
		assertEquals(task.getProject().getId(), response.getProject().getId());
		
		// failed to save task
		task.setId(null);
		TaskException taskException = assertThrows(TaskException.class, () -> taskService.createTask(request));
		assertEquals(IExceptionConstants.TASK_CREATION_FAIL, taskException.getMessage());
		
		when(projectRepository.existsById(projectId)).thenReturn(false);

		
		// project doesn't exist for given projectId
		taskException = assertThrows(TaskException.class, () -> taskService.createTask(request));
		assertEquals(IExceptionConstants.PROJECT_NOT_FOUND, taskException.getMessage());
		
		when(userRepository.existsById(assignedToId)).thenReturn(false);
		
		// user not exists for given assignedToId
		taskException = assertThrows(TaskException.class, () -> taskService.createTask(request));
		assertEquals(IExceptionConstants.USER_NOT_FOUND_FOR_ASSIGN_ID, taskException.getMessage());

		if (!mockStatic.isClosed())
			mockStatic.close();
	}
	
	@Test
	void testFilterTasks() { 
		TaskRequest request = TestDataUtil.getTaskRequest();
		User user = TestDataUtil.getUser();
		Task task = TestDataUtil.getTask();
		Page<Task> page = new PageImpl<>(List.of(task), TestDataUtil.getPageable(), 1);
		
		MockedStatic<CommonUtil> mockStatic = Mockito.mockStatic(CommonUtil.class);
		mockStatic.when(() -> CommonUtil.getUserEmail()).thenReturn(TestDataUtil.getUserEmail());
		when(userRepository.findByEmail(CommonUtil.getUserEmail())).thenReturn(Optional.of(user));
		when(taskRepository.searchTasks(request.getProjectId(), user.getId(), 
				request.getStatus(), request.getPriority(), request.getDueDate(), request.getSearch(),
				request.getPageable())).thenReturn(page);
		
		// success scenario
		TaskListResponse response = taskService.filterTasks(request);
		assertEquals(1, response.getTotalElements());
		TaskResponse taskResponse = response.getTasksList().get(0);
		assertEquals(task.getId(), taskResponse.getId());
		assertEquals(task.getTitle(), taskResponse.getTitle());
		assertEquals(task.getAssignedTo().getId(), taskResponse.getAssignedTo().getId());
		
		when(taskRepository.searchTasks(request.getProjectId(),  null, 
				request.getStatus(), request.getPriority(), request.getDueDate(), request.getSearch(),
				request.getPageable())).thenReturn(page);
		
		// assigned to string not aligned with me
		request.setAssignedTo("task_me");
		response = taskService.filterTasks(request);
		assertEquals(task.getId(), taskResponse.getId());

		// assigned to string is null
		request.setAssignedTo(null);
		response = taskService.filterTasks(request);
		assertEquals(task.getId(), taskResponse.getId());
		
		if (!mockStatic.isClosed())
			mockStatic.close();
	}
	
	@Test
	void testGetTask() { 
		Long taskId = 1L;
		Task task = TestDataUtil.getTask();
		SubTask subTask = TestDataUtil.getSubTask();
		Comment comment = TestDataUtil.getComment();
		
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
		when(subTaskRepository.findByTask(task)).thenReturn(List.of(subTask));
		when(commentRepository.findByTask(task)).thenReturn(List.of(comment));
		
		// success scenario
		TaskResponse taskResponse = taskService.getTask(taskId);
		CommentResponse commentResponse = taskResponse.getComments().get(0);
		SubTaskResponse subTaskResponse = taskResponse.getSubtasks().get(0);
		assertEquals(task.getId(), taskResponse.getId());
		assertEquals(task.getTitle(), taskResponse.getTitle());
		assertEquals(task.getAssignedTo().getId(), taskResponse.getAssignedTo().getId());
		assertEquals(task.getAssignedTo().getEmail(), taskResponse.getAssignedTo().getEmail());
		assertEquals(comment.getId(), commentResponse.getId());
		assertEquals(comment.getContent(), commentResponse.getContent());
		assertEquals(subTask.getId(), subTaskResponse.getId());
		assertEquals(subTask.getTitle(), subTaskResponse.getTitle());
		
		// case when task not found for given id
		when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
		TaskException taskException = assertThrows(TaskException.class, () -> taskService.getTask(taskId));
		assertEquals(IExceptionConstants.TASK_NOT_FOUND, taskException.getMessage());
		
	}
	
	@Test
	void testEditTask() {
		Long taskId = 1L;
		Task task = TestDataUtil.getTask();
		User user = TestDataUtil.getUser();
		TaskCreationRequest request = TestDataUtil.getTaskCreationRequest();
		Long assignedToId = request.getAssignedToId();
		
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
		when(userRepository.existsById(assignedToId)).thenReturn(true);
		when(userRepository.findById(assignedToId)).thenReturn(Optional.of(user));
		when(taskRepository.save((Task) any())).thenReturn(task);
		
		// task edited successfully
		TaskResponse taskResponse = taskService.editTask(request, taskId);
		assertEquals(task.getId(), taskResponse.getId());
		assertEquals(task.getTitle(), taskResponse.getTitle());
		
		when(userRepository.existsById(assignedToId)).thenReturn(false);

		// user not found for given assignedToId
		TaskException taskException = assertThrows(TaskException.class, () -> taskService.editTask(request, taskId));
		assertEquals(IExceptionConstants.USER_NOT_FOUND_FOR_ASSIGN_ID, taskException.getMessage());

		when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
		
		// task not found for given taskID
		taskException = assertThrows(TaskException.class, () -> taskService.editTask(request, taskId));
		assertEquals(IExceptionConstants.TASK_NOT_FOUND, taskException.getMessage());
	}
	
	@Test
	void testChangeTaskStatus() {
		Long taskId = 1L;
		TaskStatus newStatus = TaskStatus.IN_PROGRESS;
		
		when(taskRepository.existsById(taskId)).thenReturn(true);
		when(taskRepository.updateStatus(newStatus, taskId)).thenReturn(1);
		
		// task status changed
		String changedTaskStatus = taskService.changeTaskStatus(newStatus, taskId);
		assertEquals(IResponseConstants.STATUS_CHANGE_SUCCESS, changedTaskStatus);
		
		when(taskRepository.updateStatus(newStatus, taskId)).thenReturn(0);
		
		// fail to change task status
		TaskException taskException = assertThrows(TaskException.class, () -> taskService.changeTaskStatus(newStatus, taskId));
		assertEquals(IExceptionConstants.TASK_STATUS_CHANGE_FAILED, taskException.getMessage());

		when(taskRepository.existsById(taskId)).thenReturn(false);
		
		// task not found for give id
		taskException = assertThrows(TaskException.class, () -> taskService.changeTaskStatus(newStatus, taskId));
		assertEquals(IExceptionConstants.TASK_NOT_FOUND, taskException.getMessage());
	}
	
	@Test
	void testPublishComment() {
		Task task = TestDataUtil.getTask();
		Long taskId = 1L;
		String content = "Please assigned to relevant team";
		Comment comment = TestDataUtil.getComment();
		User user = TestDataUtil.getUser();
		
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
		MockedStatic<CommonUtil> mockStatic = Mockito.mockStatic(CommonUtil.class);
		mockStatic.when(() -> CommonUtil.getUserEmail()).thenReturn(TestDataUtil.getUserEmail());
		when(userRepository.findByEmail(CommonUtil.getUserEmail())).thenReturn(Optional.of(user));
		when(commentRepository.save((Comment) any())).thenReturn(comment);
		
		// comment saved successfully
		CommentResponse commentResponse = taskService.publishComment(taskId, content);
		assertEquals(comment.getId(), commentResponse.getId());
		assertEquals(comment.getContent(), commentResponse.getContent());
		assertEquals(comment.getUser().getId(), commentResponse.getUser().getId());
		assertEquals(comment.getUser().getEmail(), commentResponse.getUser().getEmail());
		
		comment.setId(null);
		
		// comment save failed
		TaskException taskException = assertThrows(TaskException.class, () -> taskService.publishComment(taskId, content));
		assertEquals(IExceptionConstants.COMMENT_FAIL, taskException.getMessage());
		
		when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
		
		// task not found for given task id
		taskException = assertThrows(TaskException.class, () -> taskService.publishComment(taskId, content));
		assertEquals(IExceptionConstants.TASK_NOT_FOUND, taskException.getMessage());
		
		if (!mockStatic.isClosed())
			mockStatic.close();
	}
	
	@Test
	void testCreateSubTask() { 
		Long taskId = 1L;
		String title = "To do Figma Design";
		Task task = TestDataUtil.getTask();
		SubTask subTask = TestDataUtil.getSubTask();
		
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
		when(subTaskRepository.save((SubTask) any())).thenReturn(subTask);
		
		// subtask created
		SubTaskResponse response = taskService.createSubTask(taskId, title);
		assertEquals(subTask.getId(), response.getId());
		assertEquals(subTask.getTitle(), response.getTitle());
		assertEquals(subTask.isCompleted(), response.getCompleted());
		
		subTask.setId(null);
		
		// failed to create sub task
		TaskException taskException = assertThrows(TaskException.class, () -> taskService.createSubTask(taskId, title));
		assertEquals(IExceptionConstants.SUBTASK_CREATION_FAIL, taskException.getMessage());
		
		when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
		
		// task not found for given task id
		taskException = assertThrows(TaskException.class, () -> taskService.createSubTask(taskId, title));
		assertEquals(IExceptionConstants.TASK_NOT_FOUND, taskException.getMessage());
	}
	
	@Test
	void testChangeSubTaskStatus() { 
		Long subTaskId = 1L;
		boolean isCompleted = true;
		
		when(subTaskRepository.existsById(subTaskId)).thenReturn(true);
		when(subTaskRepository.updateSubTaskCompltedStatus(isCompleted, subTaskId)).thenReturn(1);
		
		// sub task status changed
		String changedSubTaskStatus = taskService.changeSubTaskStatus(subTaskId, isCompleted);
		assertEquals(IResponseConstants.SUBTASK_COMPLETED_UDPATE, changedSubTaskStatus);
		
		when(subTaskRepository.updateSubTaskCompltedStatus(isCompleted, subTaskId)).thenReturn(0);
		
		// fail to change sub task status
		TaskException taskException = assertThrows(TaskException.class, () -> taskService.changeSubTaskStatus(subTaskId, isCompleted));
		assertEquals(IExceptionConstants.SUBTASK_COMPLETED_CHANGE_FAILED, taskException.getMessage());

		when(subTaskRepository.existsById(subTaskId)).thenReturn(false);
		
		// sub task not found for give id
		taskException = assertThrows(TaskException.class, () -> taskService.changeSubTaskStatus(subTaskId, isCompleted));
		assertEquals(IExceptionConstants.SUBTASK_NOT_FOUND, taskException.getMessage());
	}

}
