package com.project.spring_jpa_board.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionDTO {

    private final Long id;
    private final String loginId;
    private final String name;
    private final String email;
}
