package com.project.spring_jpa_board.service;

import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.repository.PostRepository;
import com.project.spring_jpa_board.domain.service.PostService;
import com.project.spring_jpa_board.web.dto.member.MemberSession;
import com.project.spring_jpa_board.web.dto.post.PostSaveRequest;
import com.project.spring_jpa_board.web.dto.post.PostUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("게시글 저장후 DB에서 조회하면 데이터가 일치해야한다.")
    void saveTest() {
        //given
        Member member = Member.builder().loginId("tester").name("테스터").password("1111").build();
        memberRepository.save(member);

        PostSaveRequest request = new PostSaveRequest();
        request.setTitle("제목");
        request.setContent("내용");

        //when
        Long savedId = postService.savePost(member.getId(), request);

        //Then
        Post findPost = postRepository.findById(savedId).orElseThrow();

        assertThat(findPost.getTitle()).isEqualTo("제목");
        assertThat(findPost.getContent()).isEqualTo("내용");
        assertThat(findPost.getMember().getName()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("실패 : 존재하지 않은 ID로 저장 시도시 예외 발생")
    void failSave() {
        //given
        Long memberId = 999999L;
        PostSaveRequest request = new PostSaveRequest();
        request.setTitle("제목");
        request.setContent("내용");

        //when then
        assertThatThrownBy(() ->
                postService.savePost(memberId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("작성자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("성공 : 게시글 수정시 DB에 반영되고 변경 감지 작동해야함")
    void successUpdate() {
        Member member = memberRepository.save(Member.builder()
                .loginId("author").password("1234").name("작성자").build());

        Post post = postRepository.save(Post.builder()
                .title("원래 제목").content("원래 내용").member(member).build());

        MemberSession loginMember = new MemberSession(member);
        PostUpdateRequest request = new PostUpdateRequest("수정된 제목", "수정된 내용");

        //when
        postService.update(post.getId(), request, loginMember);

        //then
        Post findPost = postRepository.findById(post.getId()).orElseThrow();
        assertThat(findPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(findPost.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("실패 : 작성자가 아니면 수정 요청 시 예외발생")
    void failUpdate() {
        Member member1 = memberRepository.save(Member.builder()
                .loginId("author1").password("1234").name("작성자").build());

        Member member2 = memberRepository.save(Member.builder()
                .loginId("author2").password("1234").name("작성자").build());

        Post post = postRepository.save(Post.builder()
                .title("원래 제목").content("원래 내용").member(member1).build());

        //when
        MemberSession loginMember = new MemberSession(member2);
        PostUpdateRequest request = new PostUpdateRequest("수정된 제목", "수정된 내용");

        //then
        assertThatThrownBy(() -> postService.update(post.getId(), request, loginMember))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("해당 게시글의 작성자가 아닙니다.");
    }
}
