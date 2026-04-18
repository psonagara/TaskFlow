package com.ps.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;

import com.ps.dto.request.LoginRequest;
import com.ps.dto.request.ProjectCreationRequest;
import com.ps.dto.request.RegisterRequest;
import com.ps.dto.request.TaskCreationRequest;
import com.ps.dto.request.TaskRequest;
import com.ps.dto.response.LoginResponse;
import com.ps.entity.Comment;
import com.ps.entity.Project;
import com.ps.entity.ProjectMember;
import com.ps.entity.SubTask;
import com.ps.entity.Task;
import com.ps.entity.User;
import com.ps.enu.TaskPriority;
import com.ps.enu.TaskStatus;
import com.ps.helper.ProjectMappingHelper;

public interface TestDataUtil {
	
	public static String getUserEmail() {
		return "user@taskflow.in";
	}

	public static User getUser() {
		User user = User.builder()
				.id(1L)
				.name("Ro Sh")
				.email(getUserEmail())
				.password("$2a$10$KlWP5pWq/6P39oc5qEcUZOA0X3E.gsc.Xsjv/TUG/HnvTBmu0hDBy")
				.creationAt(LocalDateTime.now())
				.build();
		return user;
	}
	
	public static String getJwtToken() {
		return "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuZWhhLmRlc2FpQHRhc2tmbG93LmluIiwiaWF0IjoxNzc1MzE0OTU5LCJleHAiOjE3NzUzMjU3NTl9.cuUAO-m9WsvFO-VgDkaRKDtEgg44iDjXXppFL1et9_GxvJwaxmZ_0TuAUNgUvS1fRtzkcxs8rg6KRZrv1pxJyA";
	}
	
	public static RegisterRequest getRegisterRequest() {
		RegisterRequest request = RegisterRequest.builder()
				.email(TestDataUtil.getUserEmail())
				.name("Ro Sh")
				.password("123456")
				.build();
		return request;
	}
	
	public static LoginRequest getLoginRequest() {
		LoginRequest request = LoginRequest.builder()
				.email(TestDataUtil.getUserEmail())
				.password("123456")
				.build();
		return request;
	}
	
	public static LoginResponse getLoginResponse() {
		LoginResponse response = new LoginResponse();
		response.setToken(TestDataUtil.getJwtToken());
		response.setUser(ProjectMappingHelper.toProjectMemberResponse(getUser()));
		return response;
	}
	
	
	public static ProjectCreationRequest getProjectCreationRequest() {
		ProjectCreationRequest request = new ProjectCreationRequest();
		request.setName("Alpha One");
		request.setDescription("This is description of the project Alpha One");
		return request;
	}
	
	public static Project getProject() {
		Project project = Project.builder()
				.id(1L)
				.name("Alpha One")
				.description("This is description of the project Alpha One")
				.createdBy(getUser())
				.creationAt(LocalDateTime.now())
				.build();
		return project;
	}
	
	public static ProjectMember getProjectMember() { 
		ProjectMember projectMember = ProjectMember.builder()
				.id(1L)
				.project(getProject())
				.user(getUser())
				.joinedAt(LocalDateTime.now())
				.build();
		return projectMember;
	}
	
	public static TaskCreationRequest getTaskCreationRequest() { 
		TaskCreationRequest request = new TaskCreationRequest();
		request.setTitle("Design HomePage");
		request.setDescription("Here comes description of home page");
		request.setPriority(TaskPriority.LOW);
		request.setStatus(TaskStatus.TODO);
		request.setAssignedToId(1L);
		request.setDueDate(LocalDate.now().plusDays(4));
		request.setProjectId(1L);
		return request;
	}
	
	public static TaskRequest getTaskRequest() { 
		TaskRequest request = TaskRequest.builder()
				.projectId(1L)
				.assignedTo("me")
				.status(TaskStatus.TODO)
				.priority(TaskPriority.LOW)
				.dueDate(LocalDate.now().plusDays(4))
				.search("Home")
				.pageable(getPageable())
				.build();
		return request;
	}
	
	public static PageRequest getPageable() {
		return PageRequest.of(0, 10);
	}
	
	public static Task getTask() {
		Task task = Task.builder()
				.id(1L)
				.title("Design HomePage")
				.description("Here comes description of home page")
				.dueDate(LocalDate.now().plusDays(4))
				.project(getProject())
				.assignedTo(getUser())
				.createdBy(getUser())
				.createdAt(LocalDateTime.now())
				.build();
		return task;
	}
	
	public static SubTask getSubTask() { 
		SubTask subTask = new SubTask();
		subTask.setId(1L);
		subTask.setTitle("To do Figma Design");
		subTask.setTask(getTask());
		subTask.setCreatedAt(LocalDateTime.now());
		return subTask;
	}
	
	public static Comment getComment() {
		Comment comment = new Comment();
		comment.setId(1L);
		comment.setContent("Please assigned to relevant team");
		comment.setTask(getTask());
		comment.setUser(getUser());
		comment.setCreatedAt(LocalDateTime.now());
		return comment;
	}
}
