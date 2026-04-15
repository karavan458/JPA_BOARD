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
import static com.querydsl.core.types.ExpressionUtils.orderBy;

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

        // Tuple 리스트를 Map<PostId, Count>로 변환
        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(comment.post.id),
                        tuple -> tuple.get(comment.count()).intValue()
                ));
    }

    @Override
    public Page<Comment> findByPostWithPaging(Long postId, Pageable pageable) {

        List<Comment> content = queryFactory
                .selectFrom(comment)
                // 작성자 정보는 필수이므로 페치 조인으로 N+1 방지
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

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Comment> findChildrenPage(Long parentId, Pageable pageable) {
        long offset = ((long) pageable.getPageNumber() * pageable.getPageSize()) + 5;

        List<Comment> content = queryFactory
                .selectFrom(comment)
                .join(comment.member, QMember.member).fetchJoin()
                .where(comment.parent.id.eq(parentId))
                .orderBy(comment.createdAt.asc())
                .offset(offset)
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.parent.id.eq(parentId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
