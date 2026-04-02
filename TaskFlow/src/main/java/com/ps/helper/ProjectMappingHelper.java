package com.ps.helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ps.dto.request.ProjectCreationRequest;
import com.ps.dto.response.ProjectCreationResponse;
import com.ps.dto.response.ProjectListResponse;
import com.ps.dto.response.ProjectMemberList;
import com.ps.dto.response.ProjectMemberResponse;
import com.ps.dto.response.ProjectResponse;
import com.ps.entity.Project;
import com.ps.entity.ProjectMember;
import com.ps.entity.User;

public interface ProjectMappingHelper {

	public static Project toProject(ProjectCreationRequest request) {
		Project project = new Project();
		project.setName(request.getName());
		project.setDescription(request.getDescription());
		project.setCreationAt(LocalDateTime.now());
		return project;
	}
	
	public static ProjectCreationResponse toProjectCreationResponse(Project project) {
		ProjectCreationResponse response = new ProjectCreationResponse();
		response.setId(project.getId());
		response.setName(project.getName());
		response.setDescription(project.getDescription());
		response.setCreatedAt(project.getCreationAt());
		return response;
	}
	
	public static ProjectListResponse toProjectListResponse(List<Project> projects) {
		List<ProjectResponse> projectsResponse = projects.stream().map(ProjectMappingHelper::toProjectResponse).collect(Collectors.toList());
		ProjectListResponse projectListResponse = new ProjectListResponse();
		projectListResponse.setProjects(projectsResponse);
		return projectListResponse;
	}
	
	public static ProjectResponse toProjectResponse(Project project) {
		ProjectResponse projectResponse = new ProjectResponse();
		projectResponse.setCreationAt(project.getCreationAt());
		projectResponse.setDescription(project.getDescription());
		projectResponse.setId(project.getId());
		projectResponse.setName(project.getName());
		projectResponse.setUpdateAt(project.getUpdateAt());
		return projectResponse;
	}
	
	public static ProjectMemberList toProjectMemberList(List<ProjectMember> projectMembers) {
		ProjectMemberList projectMemberList = new ProjectMemberList();
		List<ProjectMemberResponse> projectMemberResponses = projectMembers.stream()
				.map(projectMember -> ProjectMappingHelper.toProjectMemberResponse(projectMember.getUser()))
				.collect(Collectors.toList());
		projectMemberList.setMembers(projectMemberResponses);
		return projectMemberList;
	}
	
	public static ProjectMemberResponse toProjectMemberResponse(User user) {
		ProjectMemberResponse response = new ProjectMemberResponse();
		response.setEmail(user.getEmail());
		response.setId(user.getId());
		response.setName(user.getName());
		return response;
	}
}
