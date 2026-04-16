package com.project.spring_jpa_board.repository;

import com.project.spring_jpa_board.config.QuerydslConfig;
import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.repository.PostRepository;
import com.project.spring_jpa_board.web.dto.post.PostSearchCondition;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @BeforeEach
    void init() {
        Member member1 = Member.builder().loginId("userA").name("작성자A").password("pass1").build();
        Member member2 = Member.builder().loginId("userB").name("작성자B").password("pass2").build();
        memberRepository.save(member1);
        memberRepository.save(member2);

        for (int i = 1; i < 11; i++) {
            postRepository.save(Post.builder()
                    .title("제목" + i)
                    .content("내용" + i)
                    .member(member1)
                    .build());
        }

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("동적 검색 테스트 : 제목 조건만 있을 경우")
    void searchByTile() {
        //given
        PostSearchCondition condition = new PostSearchCondition("제목1", null);

        //when
        Page<Post> result = postRepository.search(condition, PageRequest.of(0, 10));

        //then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        System.out.println("결과 건수 : " + result.getTotalElements());
    }

    @Test
    @DisplayName("동적 검색 테스트 : 작성자만 있을 경우")
    void searchByWriter() {
        //given
        PostSearchCondition condition = new PostSearchCondition(null, "작성자A");

        //when
        Page<Post> result = postRepository.search(condition, PageRequest.of(0, 10));

        //then
        assertThat(result.getContent()).hasSize(10);
        System.out.println("결과 건수 : " + result.getTotalElements());
    }

    @Test
    @DisplayName("페이징 테스트 : 첫 페이지 5개 조회")
    void pagingTest() {
        //given
        PostSearchCondition condition = new PostSearchCondition(null, null);

        //when
        Page<Post> result = postRepository.search(condition, PageRequest.of(0, 5));

        //then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }
}
