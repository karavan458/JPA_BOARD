package com.project.spring_jpa_board.domain.repository;

import java.util.List;
import java.util.Map;

public interface CommentRepositoryCustom {

    Map<Long, Integer> countByPostId(List<Long> postIds);
}
