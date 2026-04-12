package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query("select c from Comment c join fetch c.member where c.post.id = :postId order by c.createdAt asc")
    List<Comment> findByPostIdWithMember(@Param("postId") Long postId);

    @Query("select count(c) from Comment c where c.post.id = :postId and c.status = 'NORMAL'")
    int countNormalCommentsByPostId(@Param("postId") Long postId);
}
