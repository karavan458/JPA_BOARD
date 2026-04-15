package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommentRepositoryCustom {

    Map<Long, Integer> countByPostId(List<Long> postIds);
    Page<Comment> findByPostWithPaging(Long postId, Pageable pageable);
    Page<Comment> findChildrenPage(Long parentId, Pageable pageable);
}
