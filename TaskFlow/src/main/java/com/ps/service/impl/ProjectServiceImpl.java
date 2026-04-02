package com.ps.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ps.constant.IExceptionConstants;
import com.ps.dto.request.ProjectCreationRequest;
import com.ps.dto.response.ProjectCreationResponse;
import com.ps.dto.response.ProjectListResponse;
import com.ps.dto.response.ProjectMemberList;
import com.ps.dto.response.ProjectResponse;
import com.ps.entity.Project;
import com.ps.entity.ProjectMember;
import com.ps.entity.User;
import com.ps.enu.ProjectRole;
import com.ps.exception.ProjectException;
import com.ps.helper.ProjectMappingHelper;
import com.ps.repo.ProjectMemberRepository;
import com.ps.repo.ProjectRepository;
import com.ps.repo.UserRepository;
import com.ps.service.IProjectService;
import com.ps.util.CommonUtil;

import jakarta.transaction.Transactional;

@Service
public class ProjectServiceImpl implements IProjectService {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProjectMemberRepository projectMemberRepository;

	@Override
	@Transactional
	public ProjectCreationResponse createProject(ProjectCreationRequest request) {
		boolean projectExists = projectRepository.existsByName(request.getName());
		if (projectExists)
			throw new ProjectException(IExceptionConstants.PROJECT_EXIST, HttpStatus.CONFLICT);
		
		//create new project
		String userEmail = CommonUtil.getUserEmail();
		User user = userRepository.findByEmail(userEmail).get();
		Project project = ProjectMappingHelper.toProject(request);
		project.setCreatedBy(user);
		project = projectRepository.save(project);
		if (project.getId() == null) {
			throw new ProjectException(IExceptionConstants.PROJECT_CREATION_FAIL);
		}
		
		//save user as owner for project
		ProjectMember projectMember = new ProjectMember();
		projectMember.setProject(project);
		projectMember.setRole(ProjectRole.OWNER);
		projectMember.setUser(user);
		projectMember = projectMemberRepository.save(projectMember);
		if (projectMember.getId() == null) {
			throw new ProjectException(IExceptionConstants.PROJECT_MEMBER_CREATION_FAIL);
		}
		
		return ProjectMappingHelper.toProjectCreationResponse(project);
	}

	@Override
	public ProjectListResponse getProjects() {
		List<Project> projects = projectRepository.findAll();
		return ProjectMappingHelper.toProjectListResponse(projects);
	}

	@Override
	public ProjectResponse getProject(Long id) {
		return ProjectMappingHelper.toProjectResponse(getProjectById(id));
	}

	@Override
	public ProjectMemberList getProjectMembers(Long id) {
		Project project = getProjectById(id);
		List<ProjectMember> projectMembers = projectMemberRepository.findByProject(project);
		return ProjectMappingHelper.toProjectMemberList(projectMembers);
	}
	
	public Project getProjectById(Long id) {
		Optional<Project> optional = projectRepository.findById(id);
		if (optional.isEmpty()) {
			throw new ProjectException(IExceptionConstants.PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		}
		return optional.get();
	}

}
