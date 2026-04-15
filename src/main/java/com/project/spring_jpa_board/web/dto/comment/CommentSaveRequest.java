package com.project.spring_jpa_board.web.dto.comment;

import com.project.spring_jpa_board.domain.entity.Comment;
import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentSaveRequest {

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
    private Long parentId;

    public Comment toEntity(Member member, Post post, Comment parent) {
        return Comment.builder()
                .content(this.content)
                .member(member)
                .post(post)
                .parent(parent)
                .build();
    }
}