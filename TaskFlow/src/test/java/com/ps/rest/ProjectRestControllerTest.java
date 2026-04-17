/**
 * 
 */
package com.ps.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ps.constant.IResponseConstants;
import com.ps.dto.request.ProjectCreationRequest;
import com.ps.dto.response.ProjectCreationResponse;
import com.ps.dto.response.ProjectListResponse;
import com.ps.dto.response.ProjectMemberList;
import com.ps.dto.response.ProjectResponse;
import com.ps.entity.Project;
import com.ps.entity.ProjectMember;
import com.ps.filter.SecurityFilter;
import com.ps.helper.ProjectMappingHelper;
import com.ps.service.IProjectService;
import com.ps.util.RestUtil;
import com.ps.util.TestDataUtil;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = ProjectRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class))
class ProjectRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private IProjectService projectService;

	@Test
	void testCreateProject() throws JsonProcessingException, Exception {
		ProjectCreationRequest request = TestDataUtil.getProjectCreationRequest();
		ProjectCreationResponse projectCreationResponse = ProjectMappingHelper.toProjectCreationResponse(TestDataUtil.getProject());
		
		when(projectService.createProject(request)).thenReturn(projectCreationResponse);
		
		mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestUtil.toJsonString(request)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.message").value(IResponseConstants.PROJECT_CREATION_SUCESS))		
		.andExpect(jsonPath("$.content.id").value(projectCreationResponse.getId()))	
		.andExpect(jsonPath("$.content.name").value(projectCreationResponse.getName()))	
		.andExpect(jsonPath("$.content.description").value(projectCreationResponse.getDescription()));	
	}
	
	
	@Test
	void testGetProjects() throws Exception {
		Project project = TestDataUtil.getProject();
		ProjectListResponse projectListResponse = ProjectMappingHelper.toProjectListResponse(List.of(project));
		
		when(projectService.getProjects()).thenReturn(projectListResponse);
		
		mockMvc.perform(get("/api/projects")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.projects[0].id").value(project.getId()))
		.andExpect(jsonPath("$.content.projects[0].name").value(project.getName()))
		.andExpect(jsonPath("$.content.projects[0].description").value(project.getDescription()));
	}
	
	@Test
	void testGetProject() throws Exception {
		Long projectId = 1L;
		Project project = TestDataUtil.getProject();
		ProjectResponse projectResponse = ProjectMappingHelper.toProjectResponse(project);
		
		when(projectService.getProject(projectId)).thenReturn(projectResponse);
		
		mockMvc.perform(get("/api/projects/{id}", projectId)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.id").value(project.getId()))
		.andExpect(jsonPath("$.content.name").value(project.getName()))
		.andExpect(jsonPath("$.content.description").value(project.getDescription()));
	}

	@Test
	void testGetProjectMembers() throws Exception {
		Long projectId = 1L;
		ProjectMember projectMember = TestDataUtil.getProjectMember();
		ProjectMemberList projectMemberList = ProjectMappingHelper.toProjectMemberList(List.of(projectMember));
		
		when(projectService.getProjectMembers(projectId)).thenReturn(projectMemberList);
		
		mockMvc.perform(get("/api/projects/{id}/members", projectId)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.members[0].id").value(projectMember.getUser().getId()))
		.andExpect(jsonPath("$.content.members[0].email").value(projectMember.getUser().getEmail()))
		.andExpect(jsonPath("$.content.members[0].name").value(projectMember.getUser().getName()));
	}

}
