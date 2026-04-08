package com.project.spring_jpa_board.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionDTO {

    private String loginId;
    private String name;
    private String email;
}
