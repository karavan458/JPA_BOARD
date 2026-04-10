package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> findAllWithMember();
}
