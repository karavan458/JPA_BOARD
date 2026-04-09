package com.project.spring_jpa_board.domain.service;

import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.web.dto.member.JoinDTO;
import com.project.spring_jpa_board.web.dto.member.LoginDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Member login(LoginDTO loginDTO) {

        Member member = memberRepository.findByLoginId(loginDTO.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if(!member.getPassword().equals(loginDTO.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    @Transactional
    public Long join(JoinDTO joinDTO) {
        validateDuplicateMember(joinDTO.getLoginId());

        Member member = Member.createMember(joinDTO);

        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(String loginId) {
        memberRepository.findByLoginId(loginId)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }
}
