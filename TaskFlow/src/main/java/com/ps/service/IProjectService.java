package com.ps.service;

import com.ps.dto.request.ProjectCreationRequest;
import com.ps.dto.response.ProjectCreationResponse;
import com.ps.dto.response.ProjectListResponse;
import com.ps.dto.response.ProjectMemberList;
import com.ps.dto.response.ProjectResponse;

public interface IProjectService {

	public ProjectCreationResponse createProject(ProjectCreationRequest request);
	public ProjectListResponse getProjects();
	public ProjectResponse getProject(Long id);
	public ProjectMemberList getProjectMembers(Long id);
}
