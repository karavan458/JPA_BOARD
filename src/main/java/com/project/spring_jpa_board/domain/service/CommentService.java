package com.project.spring_jpa_board.domain.service;

import com.project.spring_jpa_board.domain.entity.Comment;
import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.repository.CommentRepository;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.repository.PostRepository;
import com.project.spring_jpa_board.web.dto.comment.CommentRequestDTO;
import com.project.spring_jpa_board.web.dto.comment.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long saveComment(CommentRequestDTO requestDTO) {
        Post post = postRepository.findById(requestDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 입니다."));

        Comment comment = new Comment(requestDTO.getContent(), member, post);

        if (requestDTO.getParentId() != null) {
            Comment parent = commentRepository.findById(requestDTO.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다."));

            parent.addChildComment(comment);
        }

        return commentRepository.save(comment).getId();
    }

    public Page<CommentResponseDTO> getComments(Long postId, Pageable pageable) {
        Page<Comment> parentPage = commentRepository.findByPostWithPaging(postId, pageable);

        if (parentPage.isEmpty()) {
            return Page.empty(pageable);
        }

        return parentPage.map(CommentResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getCommentPage(Long postId, Pageable pageable) {

        Page<Comment> commentPage = commentRepository.findByPostWithPaging(postId, pageable);
        return commentPage.map(CommentResponseDTO::new);
    }

    @Transactional
    public void delete(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 입니다."));

        if(!comment.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        comment.delete();
    }
}
