package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;

import static com.project.spring_jpa_board.domain.entity.QPost.post;
import static com.project.spring_jpa_board.domain.entity.QMember.member;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findAllWithMember() {
        return queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .orderBy(post.createdAt.desc())
                .fetch();
    }
}