package com.project.spring_jpa_board.web.dto.member;

import com.project.spring_jpa_board.domain.entity.Member;
import lombok.Getter;

@Getter
public class MemberSession {

    private final Long id;
    private final String loginId;
    private final String name;

    public MemberSession(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.name = member.getName();
    }
}
