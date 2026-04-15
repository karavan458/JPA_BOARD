package com.project.spring_jpa_board.web.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostUpdateRequest {

    @NotBlank(message = "제목은 필수 입력 사항입니다.")
    @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
    private final String title;

    @NotBlank(message = "내용은 필수 입력 사항입니다.")
    private final String content;

    public PostUpdateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
