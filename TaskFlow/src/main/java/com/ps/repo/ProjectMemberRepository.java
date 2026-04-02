package com.ps.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Project;
import com.ps.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

	List<ProjectMember> findByProject(Project project);
}
