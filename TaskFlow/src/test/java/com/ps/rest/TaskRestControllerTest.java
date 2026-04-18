package com.ps.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.ps.enu.TaskStatus;
import com.ps.filter.SecurityFilter;
import com.ps.helper.TaskMappingHelper;
import com.ps.service.ITaskService;
import com.ps.util.RestUtil;
import com.ps.util.TestDataUtil;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = TaskRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class))
class TaskRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private ITaskService taskService;

	@Test
	void testCreateTask() throws JsonProcessingException, Exception {
		TaskCreationRequest request = TestDataUtil.getTaskCreationRequest();
		TaskResponse taskResponse = TaskMappingHelper.toTaskResponse(TestDataUtil.getTask());
		
		when(taskService.createTask(request)).thenReturn(taskResponse);
		
		mockMvc.perform(post("/api/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestUtil.toJsonString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.message").value(IResponseConstants.TASK_CREATED_SUCESS))
		.andExpect(jsonPath("$.content.id").value(taskResponse.getId()));
	}

	@Test
	void testFilterTasks() throws JsonProcessingException, Exception {
		Task task = TestDataUtil.getTask();
		Page<Task> page = new PageImpl<>(List.of(task), TestDataUtil.getPageable(), 1);
		TaskListResponse taskListResponse = TaskMappingHelper.toTaskListResponse(page);
		
		when(taskService.filterTasks((TaskRequest) any())).thenReturn(taskListResponse);
		
		mockMvc.perform(get("/api/tasks")
				.param("projectId", "1")
				.param("assignedTo", "me")
				.param("status", "TODO")
				.param("priority", "LOW")
				.param("dueDate", "2026-04-22")
				.param("search", "Home")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.tasksList[0].id").value(task.getId()))
		.andExpect(jsonPath("$.content.tasksList[0].title").value(task.getTitle()));
	}
	
	@Test
	void testGetTask() throws Exception {
		Long taskId = 1L;
		Task task = TestDataUtil.getTask();
		TaskResponse taskResponse = TaskMappingHelper.toTaskResponse(task);
		
		when(taskService.getTask(taskId)).thenReturn(taskResponse);
		
		mockMvc.perform(get("/api/tasks/{id}", taskId)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.id").value(task.getId()))
		.andExpect(jsonPath("$.content.title").value(task.getTitle()));
	}
	
	@Test
	void testEditTask() throws JsonProcessingException, Exception { 
		TaskCreationRequest request = TestDataUtil.getTaskCreationRequest();
		Long taskId = 1L;
		Task task = TestDataUtil.getTask();
		TaskResponse taskResponse = TaskMappingHelper.toTaskResponse(task);
		
		when(taskService.editTask(request, taskId)).thenReturn(taskResponse);
		
		mockMvc.perform(put("/api/tasks/{id}", taskId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestUtil.toJsonString(request)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.id").value(taskResponse.getId()))
		.andExpect(jsonPath("$.content.title").value(taskResponse.getTitle()));
	}
	
	@Test
	void testChangeTaskStatus() throws JsonProcessingException, Exception { 
		Long taskId = 1L;
		
		when(taskService.changeTaskStatus(TaskStatus.IN_PROGRESS, taskId)).thenReturn(IResponseConstants.STATUS_CHANGE_SUCCESS);
		
		mockMvc.perform(put("/api/tasks/{id}/status", taskId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestUtil.toJsonString(Map.of("status", TaskStatus.IN_PROGRESS))))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.STATUS_CHANGE_SUCCESS));
	}
	
	@Test
	void testPublishComment() throws JsonProcessingException, Exception {
		Long taskId = 1L;
		String content = "Please assigned to relevant team";
		Comment comment = TestDataUtil.getComment();
		CommentResponse commentResponse = TaskMappingHelper.toCommentResponse(comment);
		
		when(taskService.publishComment(taskId, content)).thenReturn(commentResponse);
		
		mockMvc.perform(post("/api/tasks/{id}/comments", taskId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestUtil.toJsonString(Map.of("content", content))))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.message").value(IResponseConstants.COMMENT_SUCCESS))
		.andExpect(jsonPath("$.content.content").value(content));
	}
	
	@Test
	void testCreateSubTask() throws JsonProcessingException, Exception {
		Long taskId = 1L;
		String title = "To do Figma Design";
		SubTask subTask = TestDataUtil.getSubTask();
		SubTaskResponse subTaskResponse = TaskMappingHelper.toSubTaskResponse(subTask);
		
		when(taskService.createSubTask(taskId, title)).thenReturn(subTaskResponse);
		
		mockMvc.perform(post("/api/tasks/{id}/subtasks", taskId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestUtil.toJsonString(Map.of("title", title))))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.message").value(IResponseConstants.SUBTASK_CREATED_SUCCESS))
		.andExpect(jsonPath("$.content.title").value(title));
	}
	
	@Test
	void testChangeSubTaskStatus() throws Exception { 
		Long subTaskId = 1L;
		boolean isCompleted = true;
		
		when(taskService.changeSubTaskStatus(subTaskId, isCompleted)).thenReturn(IResponseConstants.SUBTASK_COMPLETED_UDPATE);
		
		mockMvc.perform(put("/api/tasks/subtask/{id}/{isCompleted}", subTaskId, isCompleted)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value(IResponseConstants.SUBTASK_COMPLETED_UDPATE));
	}
}
