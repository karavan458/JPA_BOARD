package com.project.spring_jpa_board.web.dto.comment;

import com.project.spring_jpa_board.domain.entity.Comment;
import lombok.Getter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class CommentResponseDTO {
    private final Long id;
    private final String content;
    private final String writerName;
    private final String createdAt;
    private final List<CommentResponseDTO> children;
    private final Long memberId;
    private final int totalChildrenCount;

    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getDisplayContent();
        this.writerName = comment.getMember().getName();
        this.createdAt = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.children = comment.getChildren().stream()
                .map(CommentResponseDTO::new)
                .toList();
        this.memberId = comment.getMember().getId();
        this.totalChildrenCount = comment.getChildren().size();
    }
}