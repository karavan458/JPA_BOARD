package com.project.spring_jpa_board.web.dto.post;

import com.project.spring_jpa_board.domain.entity.Post;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class PostDetailDTO {
    Long id;
    String title;
    String content;
    String writerName;
    Long writerId;
    LocalDateTime createdAt;

    public static PostDetailDTO from(Post post) {
        return new PostDetailDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getName(),
                post.getMember().getId(),
                post.getCreatedAt()
        );
    }
}
