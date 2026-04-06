package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepsoitory extends JpaRepository<Member, Long> {
}
