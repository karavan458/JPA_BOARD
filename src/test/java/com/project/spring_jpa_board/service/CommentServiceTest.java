package com.project.spring_jpa_board.service;

import com.project.spring_jpa_board.domain.entity.Comment;
import com.project.spring_jpa_board.domain.entity.CommentStatus;
import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.repository.CommentRepository;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.repository.PostRepository;
import com.project.spring_jpa_board.domain.service.CommentService;
import com.project.spring_jpa_board.web.dto.comment.CommentSaveRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class CommentServiceTest {
    @Autowired
    CommentService commentService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("대댓글 저장시 부모 댓글 ID를 지정하면 계층 구조가 DB에 성립해야함")
    void saveReply_Success() {
        //given
        Member member = memberRepository.save(Member.builder()
                .loginId("author41").password("1234").name("작성자").build());
        Post post = postRepository.save(Post.builder().title("제목").content("내용").member(member).build());

        Comment parent = commentRepository.save(Comment.builder()
                .content("부모 댓글")
                .post(post)
                .member(member)
                .build());

        CommentSaveRequest replyRequest = new CommentSaveRequest();
        replyRequest.setContent("자식 댓글");
        replyRequest.setParentId(parent.getId());

        //when
        Long replyId = commentService.saveComment(post.getId(), member.getId(), replyRequest);

        //then
        Comment findReply = commentRepository.findById(replyId).orElseThrow();

        assertThat(findReply.getContent()).isEqualTo("자식 댓글");
        assertThat(findReply.getParent().getId()).isEqualTo(parent.getId());
        assertThat(parent.getChildren().contains(findReply));
    }

    @Test
    @DisplayName("성공 : 댓글 삭제시 실제 삭제가 아닌 상태가 DELETED로 변경되어야 함")
    void deleteComment() {
        //given
        Member member = memberRepository.save(Member.builder()
                .loginId("author41").password("1234").name("작성자").build());
        Post post = postRepository.save(Post.builder().title("제목").content("내용").member(member).build());

        Comment comment = commentRepository.save(Comment.builder()
                .content("댓글")
                .post(post)
                .member(member)
                .build());

        // when
        commentService.delete(comment.getId(), member.getId());

        //then
        Comment findComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(findComment).isNotNull();
        assertThat(findComment.getStatus()).isEqualTo(CommentStatus.DELETED);
        assertThat(findComment.getDisplayContent()).isEqualTo("삭제된 댓글입니다.");
    }
}
