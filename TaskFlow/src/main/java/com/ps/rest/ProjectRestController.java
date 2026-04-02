package com.ps.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ps.constant.IMappingConstants;
import com.ps.constant.IResponseConstants;
import com.ps.dto.request.ProjectCreationRequest;
import com.ps.dto.response.ProjectCreationResponse;
import com.ps.dto.response.ProjectListResponse;
import com.ps.dto.response.ProjectMemberList;
import com.ps.dto.response.ProjectResponse;
import com.ps.service.IProjectService;
import com.ps.util.CommonUtil;

@RestController
@RequestMapping(IMappingConstants.PROJECT_API_PATH)
public class ProjectRestController {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProjectRestController.class);
	
	@Autowired
	private IProjectService projectService;

	@PostMapping
	public ResponseEntity<?> createProject(@RequestBody ProjectCreationRequest request) {
		LOG.debug("Enter in ProjectRestController.createProject, " + request);
		ProjectCreationResponse response = projectService.createProject(request);
		return CommonUtil.prepareApiResponse(IResponseConstants.PROJECT_CREATION_SUCESS, HttpStatus.CREATED, response);
	}
	
	@GetMapping
	public ResponseEntity<?> getProjects() {
		LOG.debug("Enter in ProjectRestController.getProjects");
		ProjectListResponse projects = projectService.getProjects();
		return CommonUtil.prepareResponseWithContent(projects, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getProject(@PathVariable("id") Long id) {
		LOG.debug("Enter in ProjectRestController.getProject, " + id);
		ProjectResponse project = projectService.getProject(id);
		return CommonUtil.prepareResponseWithContent(project, HttpStatus.OK);
	}
	
	@GetMapping("/{id}/members")
	public ResponseEntity<?> getProjectMembers(@PathVariable("id") Long id) {
		LOG.debug("Enter in ProjectRestController.getProjectMembers, " + id);
		ProjectMemberList projectMembers = projectService.getProjectMembers(id);
		return CommonUtil.prepareResponseWithContent(projectMembers, HttpStatus.OK);
	}
}
