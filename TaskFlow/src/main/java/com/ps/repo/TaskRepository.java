package com.ps.repo;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ps.entity.Task;
import com.ps.enu.TaskPriority;
import com.ps.enu.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("""
			SELECT t FROM Task t
			WHERE (:projectId IS NULL OR t.project.id = :projectId)
			AND (:userId IS NULL OR t.assignedTo.id = :userId)
			AND (:status IS NULL OR t.status = :status)
			AND (:priority IS NULL OR t.priority = :priority)
			AND (:dueDate IS NULL OR t.dueDate = :dueDate)
			AND (:search IS NULL OR t.title LIKE %:search%)
			""")
	Page<Task> searchTasks(@Param("projectId") Long projectId, @Param("userId") Long userId,
			@Param("status") TaskStatus status, @Param("priority") TaskPriority priority, 
			@Param("dueDate") LocalDate dueDate, @Param("search") String search, Pageable pageable);
	
	@Query("UPDATE Task t SET t.status = :status WHERE t.id = :id")
	@Modifying
	int updateStatus(@Param("status") TaskStatus status, @Param("id") Long id);
}
