package com.project.spring_jpa_board.web.dto.member;

import com.project.spring_jpa_board.domain.entity.Member;
import lombok.Getter;

@Getter
public class MemberMyPageResponse {

    private final String name;
    private final String email;
    private final Long postCount;
    private final Long commentCount;

    public MemberMyPageResponse(Member member, long postCount, long commentCount) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.postCount = postCount;
        this.commentCount = commentCount;
    }
}
