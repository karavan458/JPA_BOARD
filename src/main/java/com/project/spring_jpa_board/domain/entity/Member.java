package com.project.spring_jpa_board.domain.entity;

import com.project.spring_jpa_board.web.dto.member.JoinDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String email;
    private String phone;

    @Embedded
    private Address address;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Member(Long id, String loginId, String password, String name, String email, String phone, Address address, LocalDateTime createdAt) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdAt = createdAt;
    }

    public static Member createMember(JoinDTO joinDTO) {
        Member member = new Member();
        member.loginId = joinDTO.getLoginId();
        member.password = joinDTO.getPassword();
        member.name = joinDTO.getName();
        member.email = joinDTO.getEmail();
        member.phone = joinDTO.getPhone();

        member.address = new Address(joinDTO.getCity(), joinDTO.getStreet(), joinDTO.getZipcode());
        member.createdAt = LocalDateTime.now();

        return member;
    }
}
