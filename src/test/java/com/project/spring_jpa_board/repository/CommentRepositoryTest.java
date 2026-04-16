package com.project.spring_jpa_board.repository;

import com.project.spring_jpa_board.config.QuerydslConfig;
import com.project.spring_jpa_board.domain.entity.Comment;
import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.repository.CommentRepository;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace =AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    Member member;
    Post post;

    @BeforeEach
    void init() {
        member = Member.builder().loginId("userC").name("댓글러").password("pass3").build();
        memberRepository.save(member);

        post = Post.builder().title("테스트 게시글").content("내용").member(member).build();
        postRepository.save(post);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("게시글 리스트용 댓글 수 일괄 조회 - NORMAL 만 카운터")
    void countByPostIdTest() {
        //given
        Post post2 = Post.builder().title("테스트 게시글2").content("내용").member(member).build();
        postRepository.save(post2);

        commentRepository.save(Comment.builder().content("정상1").post(post).member(member).build());
        commentRepository.save(Comment.builder().content("정상2").post(post).member(member).build());
        Comment deletedComment = commentRepository.save(Comment.builder().content("삭제될 댓글").post(post2).member(member).build());
        deletedComment.delete();
        commentRepository.save(deletedComment);

        commentRepository.save(Comment.builder().content("정상3").post(post2).member(member).build());

        em.flush();
        em.clear();

        //when
        Map<Long, Integer> counts = commentRepository.countByPostId(List.of(post.getId(), post2.getId()));

        //then
        assertThat(counts.get(post.getId())).isEqualTo(2);
        assertThat(counts.get(post2.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("부모 댓글 페이징 조회 - 대댓글은 제외")
    void findByPostWithPagingTest() {
        //given
        for(int i = 1; i < 6; i++) {
            Comment parent = Comment.builder().content("부모 " + i).post(post).member(member).build();
            commentRepository.save(parent);

            if(i == 1) {
                for(int j = 1; j < 4; j++) {
                    commentRepository.save(Comment.builder().content("대댓글 " + j).post(post).member(member).parent(parent).build());
                }
            }
        }

        em.flush();
        em.clear();

        //when
        Page<Comment> result = commentRepository.findByPostWithPaging(post.getId(), PageRequest.of(0, 10));

        //then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent().get(0).getMember().getName()).isEqualTo("댓글러");
    }

    @Test
    @DisplayName("부모 ID 리스트로 자식 댓글 일괄 조회")
    void findChildrenByParentTest() {
        //given
        Comment c1 = commentRepository.save(Comment.builder().content("부모1").post(post).member(member).build());
        Comment c2 = commentRepository.save(Comment.builder().content("부모2").post(post).member(member).build());

        commentRepository.save(Comment.builder().content("자식1").post(post).member(member).parent(c1).build());
        commentRepository.save(Comment.builder().content("자식2").post(post).member(member).parent(c1).build());
        commentRepository.save(Comment.builder().content("자식3").post(post).member(member).parent(c2).build());

        em.flush();
        em.clear();

        //when
        List<Comment> child = commentRepository.findChildrenByParentIds(List.of(c1.getId(), c2.getId()));

        //then
        assertThat(child).hasSize(3);
        assertThat(child).extracting("content").containsExactly("자식1", "자식2", "자식3");
    }
}
