package com.project.spring_jpa_board.web.dto.post;

import com.project.spring_jpa_board.domain.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListDTO {
    private final Long id;
    private final String title;
    private final String writerName;
    private final LocalDateTime createdAt;
    private final int commentCount;

    public PostListDTO(Post post, int commentCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.writerName = post.getMember().getName();
        this.createdAt = post.getCreatedAt();
        this.commentCount = commentCount;
    }
}

