package com.project.spring_jpa_board.domain.service;

import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.repository.MemberRepository;
import com.project.spring_jpa_board.domain.repository.PostRepository;
import com.project.spring_jpa_board.web.dto.member.SessionDTO;
import com.project.spring_jpa_board.web.dto.post.PostSaveDTO;
import com.project.spring_jpa_board.web.dto.post.PostSearchCondition;
import com.project.spring_jpa_board.web.dto.post.PostUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long savePost(Long memberId, PostSaveDTO postSaveDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("작성자를 찾을 수 없습니다."));

        Post post = Post.createPost(postSaveDTO.getTitle(), postSaveDTO.getContent(), member);
        postRepository.save(post);

        return post.getId();
    }

    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));
    }

    @Transactional
    public void update(Long id, PostUpdateDTO postUpdateDTO, SessionDTO loginMember) {
        Post updatePost = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));

        validateAuthor(loginMember, updatePost);
        updatePost.update(postUpdateDTO.getTitle(), postUpdateDTO.getContent());
    }

    @Transactional
    public Long delete(Long id, SessionDTO loginMember) {
        Post deletePost = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));

        validateAuthor(loginMember, deletePost);
        postRepository.delete(deletePost);
        return id;
    }

    private static void validateAuthor(SessionDTO loginMember, Post updatePost) {
        if(!updatePost.getMember().getId().equals(loginMember.getId())) {
            throw new IllegalStateException("해당 게시글의 작성자가 아닙니다.");
        }
    }

    @Transactional(readOnly = true)
    public Page<Post> search(PostSearchCondition condition, Pageable pageable) {
        return postRepository.search(condition, pageable);
    }
}
