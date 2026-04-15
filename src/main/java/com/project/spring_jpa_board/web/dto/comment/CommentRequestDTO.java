package com.project.spring_jpa_board.web.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;

    @NotNull
    private Long postId;

    private Long memberId;

    private Long parentId;

    public static CommentRequestDTO create(String content, Long postId, Long memberId, Long parentId) {
        return new CommentRequestDTO(content, postId, memberId, parentId);
    }
}
