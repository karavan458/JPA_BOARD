package com.project.spring_jpa_board.domain.repository;

import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    // 작성자 별 게시글 필터링
    List<Post> findByMember(Member member);

    // 제목 검색
    List<Post> findByTitleContaining(String title);

    // 최신순 정렬 조회
    List<Post> findAllByOrderByCreatedAtDesc();

    long countByMemberId(Long memberId);
}
