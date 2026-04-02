package com.ps.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	boolean existsByName(String name);
}
