package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Comment;
import com.project.spring_jpa_board.domain.entity.CommentStatus;
import com.project.spring_jpa_board.domain.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.spring_jpa_board.domain.entity.QComment.comment;
import static com.project.spring_jpa_board.domain.entity.QMember.member;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, Integer> countByPostId(List<Long> postIds) {
        List<Tuple> results = queryFactory
                .select(comment.post.id, comment.count())
                .from(comment)
                .where(
                        comment.post.id.in(postIds),
                        comment.status.eq(CommentStatus.NORMAL)
                )
                .groupBy(comment.post.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.post.id),
                        tuple -> tuple.get(comment.count()).intValue()
                ));
    }

    @Override
    public Page<Comment> findByPostWithPaging(Long postId, Pageable pageable) {

        List<Comment> parents = queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.isNull()
                )
                .orderBy(comment.createdAt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.isNull()
                );

        return PageableExecutionUtils.getPage(parents, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Comment> findChildrenByParentIds(List<Long> parentIds) {
        return queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .where(
                        comment.parent.id.in(parentIds)
                )
                .orderBy(comment.createdAt.asc())
                .fetch();
    }
}
