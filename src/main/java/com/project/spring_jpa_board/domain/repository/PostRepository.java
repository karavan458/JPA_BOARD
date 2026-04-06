package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
