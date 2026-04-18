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

import com.ps.constant.IExceptionConstants;
import com.ps.dto.request.ProjectCreationRequest;
import com.ps.dto.response.ProjectCreationResponse;
import com.ps.dto.response.ProjectListResponse;
import com.ps.dto.response.ProjectMemberList;
import com.ps.dto.response.ProjectMemberResponse;
import com.ps.dto.response.ProjectResponse;
import com.ps.entity.Project;
import com.ps.entity.ProjectMember;
import com.ps.entity.User;
import com.ps.exception.ProjectException;
import com.ps.repo.ProjectMemberRepository;
import com.ps.repo.ProjectRepository;
import com.ps.repo.UserRepository;
import com.ps.util.CommonUtil;
import com.ps.util.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
	
	@InjectMocks
	private ProjectServiceImpl projectService;
	
	@Mock
	private ProjectRepository projectRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private ProjectMemberRepository projectMemberRepository;
	
	@Test
	void testCreateProject() {
		ProjectCreationRequest request = TestDataUtil.getProjectCreationRequest();
		User user = TestDataUtil.getUser();
		Project project = TestDataUtil.getProject();
		ProjectMember projectMember = TestDataUtil.getProjectMember();
		
		MockedStatic<CommonUtil> mockStatic = Mockito.mockStatic(CommonUtil.class);
		mockStatic.when(() -> CommonUtil.getUserEmail()).thenReturn(TestDataUtil.getUserEmail());
		when(projectRepository.existsByName(request.getName())).thenReturn(false);
		when(userRepository.findByEmail(TestDataUtil.getUserEmail())).thenReturn(Optional.of(user));
		when(projectRepository.save((Project) any())).thenReturn(project);
		when(projectMemberRepository.save((ProjectMember) any())).thenReturn(projectMember);
		
		// project creation success
		ProjectCreationResponse response = projectService.createProject(request);
		assertEquals(project.getId(), response.getId());
		assertEquals(project.getName(), response.getName());
		assertEquals(project.getDescription(), response.getDescription());
		
		// failed to save project member
		projectMember.setId(null);
		ProjectException projectException = assertThrows(ProjectException.class, () -> projectService.createProject(request));
		assertEquals(IExceptionConstants.PROJECT_MEMBER_CREATION_FAIL, projectException.getMessage());

		// failed to save project
		project.setId(null);
		projectException = assertThrows(ProjectException.class, () -> projectService.createProject(request));
		assertEquals(IExceptionConstants.PROJECT_CREATION_FAIL, projectException.getMessage());
		
		when(projectRepository.existsByName(request.getName())).thenReturn(true);
		//project already exists with give name
		projectException = assertThrows(ProjectException.class, () -> projectService.createProject(request));
		assertEquals(IExceptionConstants.PROJECT_EXIST, projectException.getMessage());
		
		if (!mockStatic.isClosed())
			mockStatic.close();
	}
	
	@Test
	void testGetProjects() {
		Project project = TestDataUtil.getProject();
		
		when(projectRepository.findAll()).thenReturn(List.of(project));
		
		ProjectListResponse response = projectService.getProjects();
		ProjectResponse projectResponse = response.getProjects().get(0);
		assertEquals(project.getId(), projectResponse.getId());
		assertEquals(project.getName(), projectResponse.getName());
		assertEquals(project.getDescription(), projectResponse.getDescription());
	}
	
	@Test
	void testGetProject() { 
		Long projectId = 1L;
		Project project = TestDataUtil.getProject();
		
		when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
		// success case
		ProjectResponse response = projectService.getProject(projectId);
		assertEquals(project.getId(), response.getId());
		assertEquals(project.getName(), response.getName());
		assertEquals(project.getDescription(), response.getDescription());
		
		when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
		
		// project not found
		ProjectException projectException = assertThrows(ProjectException.class, () -> projectService.getProject(projectId));
		assertEquals(IExceptionConstants.PROJECT_NOT_FOUND, projectException.getMessage());
	}
	
	@Test
	void testGetProjectMembers() { 
		Long projectId = 1L;
		Project project = TestDataUtil.getProject();
		ProjectMember projectMember = TestDataUtil.getProjectMember();
		
		when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
		when(projectMemberRepository.findByProject(project)).thenReturn(List.of(projectMember));
		
		ProjectMemberList projectMembers = projectService.getProjectMembers(projectId);
		ProjectMemberResponse projectMemberResponse = projectMembers.getMembers().get(0);
		assertEquals(projectMember.getUser().getId(), projectMemberResponse.getId());
		assertEquals(projectMember.getUser().getName(), projectMemberResponse.getName());
		assertEquals(projectMember.getUser().getEmail(), projectMemberResponse.getEmail());
	}
}
