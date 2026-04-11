package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.web.dto.post.PostSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.project.spring_jpa_board.domain.entity.QPost.post;
import static com.project.spring_jpa_board.domain.entity.QMember.member;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> search(PostSearchCondition condition, Pageable pageable) {
        // 1. 실제 데이터를 가져오는 컨텐츠 쿼리
        List<Post> content = queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .where(
                        titleContains(condition.getTitle()),
                        writerNameContains(condition.getWriter())
                )
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .join(post.member, member)
                .where(
                        titleContains(condition.getTitle()),
                        writerNameContains(condition.getWriter())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression titleContains(String titleCond) {
        return StringUtils.hasText(titleCond) ? post.title.contains(titleCond) : null;
    }

    private BooleanExpression writerNameContains(String writerCond) {
        return StringUtils.hasText(writerCond) ? member.name.contains(writerCond) : null;
    }
}