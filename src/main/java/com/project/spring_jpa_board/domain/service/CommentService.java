package com.project.spring_jpa_board.domain.service;

import com.project.spring_jpa_board.domain.repository.CommentRepsoitory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {

    private final CommentRepsoitory commentRepsoitory;

    public CommentService(CommentRepsoitory commentRepsoitory) {
        this.commentRepsoitory = commentRepsoitory;
    }
}
