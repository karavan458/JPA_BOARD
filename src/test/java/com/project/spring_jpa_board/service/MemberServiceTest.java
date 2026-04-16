package com.project.spring_jpa_board.service;

import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.service.MemberService;
import com.project.spring_jpa_board.web.dto.member.MemberJoinRequest;
import com.project.spring_jpa_board.web.dto.member.MemberLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("성공 : 회원가입 후 DB에서 조회하면 데이터 일치")
    void joinSuccess() {
        //given
        MemberJoinRequest request = new MemberJoinRequest();
        request.setLoginId("testUser");
        request.setPassword("1234");
        request.setName("테스터");

        //when
        Long savedId = memberService.join(request);

        //then
        Member findMember = memberRepository.findById(savedId).orElseThrow();
        assertThat(findMember.getLoginId()).isEqualTo("testUser");
        assertThat(findMember.getName()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("실패 : 중복 아이디로 가입 시도")
    void joinDuplicatedFail() {
        //given
        MemberJoinRequest request = new MemberJoinRequest();
        request.setLoginId("dupUser");
        request.setPassword("1234");
        request.setName("중복유저");

        memberService.join(request);

        //when , then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 존재하는 회원입니다.");
    }

    @Test
    @DisplayName("성공 : 올바른 아이디와 비밀번호로 로그인")
    void loingSuccess() {
        //given
        MemberJoinRequest request = new MemberJoinRequest();
        request.setLoginId("loginUser");
        request.setPassword("1234");
        request.setName("로그인 유저");
        memberService.join(request);

        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("loginUser");
        loginRequest.setPassword("1234");

        //when
        Member loginMember = memberService.login(loginRequest);

        //then
        assertThat(loginMember.getLoginId()).isEqualTo("loginUser");
    }

    @Test
    @DisplayName("실패 : 존재하지 않는 아이디로 로그인 시")
    void loginFail() {
        //given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("ghost");
        loginRequest.setPassword("1234");

        //when, then
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 아이디입니다.");
    }

    @Test
    @DisplayName("실패 : 비밀번호가 틀릴 경우")
    void loginFailWrongPassword() {
        //given
        //given
        MemberJoinRequest request = new MemberJoinRequest();
        request.setLoginId("loginUser");
        request.setPassword("1234");
        request.setName("로그인 유저");
        memberService.join(request);

        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("loginUser");
        loginRequest.setPassword("12345");

        //when, then
        assertThatThrownBy(() -> memberService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");
    }
}
