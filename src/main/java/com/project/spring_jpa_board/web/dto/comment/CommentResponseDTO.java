package com.project.spring_jpa_board.web.dto.comment;

import com.project.spring_jpa_board.domain.entity.Comment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

import static com.project.spring_jpa_board.domain.entity.QComment.comment;

@Getter
public class CommentResponseDTO {

    private final Long id;
    private final String content;
    private final String writerName;
    private final String createdAt;

    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writerName = comment.getMember().getName(); // 작성자 ID 대신 이름을 추출
        this.createdAt = comment.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // 가독성 있게 포맷팅
    }
}
