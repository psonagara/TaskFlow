package com.ps.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.entity.SubTask;
import com.ps.entity.Task;

public interface SubTaskRepository extends JpaRepository<SubTask, Long> {

	List<SubTask> findByTask(Task task);
	
	@Query("UPDATE SubTask s SET s.completed = :isCompleted WHERE s.id = :id")
	@Modifying
	int updateSubTaskCompltedStatus(@Param("isCompleted") boolean isCompleted, @Param("id") Long id);
}
