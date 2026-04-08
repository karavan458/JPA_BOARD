package com.project.spring_jpa_board.domain.service;

import com.project.spring_jpa_board.domain.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepsoitory;

    public CommentService(CommentRepository commentRepsoitory) {
        this.commentRepsoitory = commentRepsoitory;
    }
}
