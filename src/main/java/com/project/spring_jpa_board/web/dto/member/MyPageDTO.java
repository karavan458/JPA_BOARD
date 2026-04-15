package com.project.spring_jpa_board.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyPageDTO {

    private String name;
    private String email;
    private Long postCount;
    private Long commentCount;
}
