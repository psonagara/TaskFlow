package com.ps.util;

import java.time.LocalDateTime;

import com.ps.entity.Project;
import com.ps.entity.ProjectMember;
import com.ps.entity.User;

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
	
}
