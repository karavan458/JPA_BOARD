package com.project.spring_jpa_board.domain.service;

import com.project.spring_jpa_board.domain.repository.MemberRepsoitory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepsoitory memberRepsoitory;

    public MemberService(MemberRepsoitory memberRepsoitory) {
        this.memberRepsoitory = memberRepsoitory;
    }
}
