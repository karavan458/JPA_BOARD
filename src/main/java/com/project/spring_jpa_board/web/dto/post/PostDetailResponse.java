package com.project.spring_jpa_board.web.dto.post;

import com.project.spring_jpa_board.domain.entity.Post;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PostDetailResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String writerName;
    private final Long writerId;
    private final LocalDateTime createdAt;

    public PostDetailResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.writerName = post.getMember().getName();
        this.writerId = post.getMember().getId();
        this.createdAt = post.getCreatedAt();
    }
}