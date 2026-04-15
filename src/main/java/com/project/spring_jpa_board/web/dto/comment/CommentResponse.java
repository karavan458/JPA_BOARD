package com.project.spring_jpa_board.web.dto.comment;

import com.project.spring_jpa_board.domain.entity.Comment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class CommentResponse {
    private final Long id;
    private final String content;
    private final String writerName;
    private final String createdAt;
    private final List<CommentResponse> children;
    private final Long writerId;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getDisplayContent(); // 엔티티의 로직 활용
        this.writerName = comment.getMember().getName();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.writerId = comment.getMember().getId();

        // 자식 댓글들 또한 동일한 Response 규격으로 재귀적 조립
        this.children = comment.getChildren().stream()
                .map(CommentResponse::new)
                .toList();
    }
}