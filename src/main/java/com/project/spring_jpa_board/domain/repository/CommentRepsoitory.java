package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepsoitory extends JpaRepository<Comment, Long> {
}
