package com.project.spring_jpa_board.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Comment parent;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    public Comment(String content, Member member, Post post) {
        this.content = content;
        this.member = member;
        this.post = post;
        this.createdAt = LocalDateTime.now();
        this.status = CommentStatus.NORMAL;
    }

    public void delete() {
        this.status = CommentStatus.DELETED;
    }

    public String getDisplayContent() {
        if(this.status == CommentStatus.DELETED) {
            return "삭제된 댓글입니다.";
        }
        return this.content;
    }

    public void addChildComment(Comment child) {
        this.children.add(child);
        child.setParent(this);
    }

    private void setParent(Comment parent) {
        this.parent = parent;
    }
}
