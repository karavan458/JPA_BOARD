package com.project.spring_jpa_board.web.dto.comment;

import com.project.spring_jpa_board.domain.entity.Comment;
import lombok.Getter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentResponseDTO {

    private final Long id;
    private final String content;
    private final String writerName;
    private final String createdAt;
    private final List<CommentResponseDTO> children;
    private final Long memberId;
    private final boolean hasMoreChildren;
    private final int totalChildrenCount;

    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getDisplayContent();
        this.writerName = comment.getMember().getName();
        this.createdAt = comment.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.memberId = comment.getMember().getId();

        List<Comment> allChildren = comment.getChildren();
        this.totalChildrenCount = allChildren.size();

        this.children = allChildren.stream()
                .map(CommentResponseDTO::new)
                .collect(Collectors.toList());

        this.hasMoreChildren = false;
    }
}