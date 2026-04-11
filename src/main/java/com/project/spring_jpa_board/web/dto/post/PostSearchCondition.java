package com.project.spring_jpa_board.web.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PostSearchCondition {

    private final String title;
    private final String writer;
}
