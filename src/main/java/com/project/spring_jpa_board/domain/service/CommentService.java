package com.project.spring_jpa_board.domain.service;

import com.project.spring_jpa_board.domain.entity.Comment;
import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.repository.CommentRepository;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.repository.PostRepository;
import com.project.spring_jpa_board.web.dto.comment.CommentSaveRequest;
import com.project.spring_jpa_board.web.dto.comment.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long saveComment(Long memberId, Long postId, CommentSaveRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));
        }

        Comment comment = request.toEntity(member, post, parent);

        if(parent != null) {
            parent.addChildComment(comment);
        }

        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentPage(Long postId, Pageable pageable) {

        return commentRepository.findByPostWithPaging(postId, pageable).
            map(CommentResponse::new);
    }

    @Transactional
    public void delete(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 입니다."));

        validateAuthor(memberId, comment);
        comment.delete();
    }

    private void validateAuthor(Long memberId, Comment comment) {
        if(!comment.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
        }
    }
}
