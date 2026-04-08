package com.project.spring_jpa_board.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    private String title;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Post(String title, String content, Member member, LocalDateTime createdAt) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.createdAt = createdAt;
    }
}
