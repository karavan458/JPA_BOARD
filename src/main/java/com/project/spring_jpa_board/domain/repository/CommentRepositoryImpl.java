package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.CommentStatus;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.spring_jpa_board.domain.entity.QComment.comment;

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
}
