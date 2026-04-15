package com.project.spring_jpa_board.domain.service;

import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.repository.CommentRepository;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.repository.PostRepository;
import com.project.spring_jpa_board.web.dto.member.MemberSession;
import com.project.spring_jpa_board.web.dto.post.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long savePost(Long memberId, PostSaveRequest postSaveRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("작성자를 찾을 수 없습니다."));

        Post post = postSaveRequest.toEntity(member);
        postRepository.save(post);

        return post.getId();
    }

    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));
    }

    @Transactional
    public void update(Long id, PostUpdateRequest postUpdateRequest, MemberSession loginMember) {
        Post post = findById(id);

        validateAuthor(loginMember, post);
        post.update(postUpdateRequest.getTitle(), postUpdateRequest.getContent());
    }

    @Transactional
    public Long delete(Long id, MemberSession loginMember) {
        Post post = findById(id);

        validateAuthor(loginMember, post);
        postRepository.delete(post);
        return id;
    }

    private void validateAuthor(MemberSession loginMember, Post post) {
        if (!post.getMember().getId().equals(loginMember.getId())) {
            throw new IllegalStateException("해당 게시글의 작성자가 아닙니다.");
        }
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> search(PostSearchCondition condition, Pageable pageable) {
        Page<Post> postPage = postRepository.search(condition, pageable);

        if(postPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> postIds = postPage.getContent().stream()
                .map(Post::getId)
                .toList();

        Map<Long, Integer> commentCountMap = commentRepository.countByPostId(postIds);

        return postPage.map(post -> {
            int commentCount = commentCountMap.getOrDefault(post.getId(), 0);
            return new PostListResponse(post, commentCount);
        });
    }
}
