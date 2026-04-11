package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.web.dto.post.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<Post> search(PostSearchCondition condition, Pageable pageable);
}
