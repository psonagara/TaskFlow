package com.ps.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ps.entity.Comment;
import com.ps.entity.Task;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByTask(Task task);
}
