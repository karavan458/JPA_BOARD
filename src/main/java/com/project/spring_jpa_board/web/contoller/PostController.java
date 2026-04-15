package com.project.spring_jpa_board.web.contoller;

import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.service.CommentService;
import com.project.spring_jpa_board.domain.service.PostService;
import com.project.spring_jpa_board.web.dto.comment.CommentSaveRequest;
import com.project.spring_jpa_board.web.dto.comment.CommentResponse;
import com.project.spring_jpa_board.web.dto.member.MemberSession;
import com.project.spring_jpa_board.web.dto.post.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute("postForm", new PostSaveRequest());
        return "post/createForm";
    }

    @PostMapping("/new")
    public String create(
            @Validated @ModelAttribute("postForm") PostSaveRequest postSaveRequest,
            BindingResult bindingResult,
            @SessionAttribute(name = "loginMember") MemberSession loginMember) {

        if(bindingResult.hasErrors()) {
            return "post/createForm";
        }

        postService.savePost(loginMember.getId(), postSaveRequest);
        return "redirect:/post/list";
    }

    @GetMapping("/{postId}")
    public String read(
            @PathVariable("postId") Long postId,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {

        Post post = postService.findById(postId);
        model.addAttribute("post", new PostDetailResponse(post));

        Page<CommentResponse> comments = commentService.getCommentPage(postId, pageable);
        model.addAttribute("comments", comments);

        return "post/detail";
    }

    @PostMapping("/{postId}/comment")
    public String comment(
            @PathVariable("postId") Long postId,
            @Validated @ModelAttribute("commentRequest") CommentSaveRequest requestDTO,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @SessionAttribute(name = "loginMember", required = false) MemberSession loginMember,
            RedirectAttributes redirectAttributes) {

        if (loginMember == null) {
            return "redirect:/members/login";
        }

        commentService.saveComment(loginMember.getId(), postId, requestDTO);

        redirectAttributes.addAttribute("page", page);
        return "redirect:/post/{postId}";
    }

    @GetMapping("/list")
    public String list(
            @ModelAttribute("postSearch") PostSearchCondition condition,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        Page<PostListResponse> page = postService.search(condition, pageable);

        model.addAttribute("page", page);
        return "post/list";
    }

    @GetMapping("/{postId}/update")
    public String updateForm(
            @PathVariable("postId") Long postId,
            Model model) {

        Post post = postService.findById(postId);
        PostUpdateRequest postUpdateDTO = new PostUpdateRequest(post.getTitle(), post.getContent());

        model.addAttribute("postId", postId);
        model.addAttribute("postUpdateDTO", postUpdateDTO);
        return "post/updateForm";
    }

    @PostMapping("/{postId}/update")
    public String update(
            @PathVariable("postId") Long postId,
            @Valid @ModelAttribute("postUpdateDTO") PostUpdateRequest postUpdateRequest, // 명칭 정렬
            BindingResult bindingResult,
            @SessionAttribute(name = "loginMember") MemberSession loginMember,
            @ModelAttribute("postSearch") PostSearchCondition condition,
            @PageableDefault Pageable pageable,
            RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            return "post/updateForm";
        }

        postService.update(postId, postUpdateRequest, loginMember);

        redirectAttributes.addAttribute("page", pageable.getPageNumber());
        redirectAttributes.addAttribute("title", condition.getTitle());
        redirectAttributes.addAttribute("writer", condition.getWriter());

        return "redirect:/post/list";
    }

    @PostMapping("/{postId}/delete")
    public String delete(@PathVariable("postId") Long postId,
            @SessionAttribute(name = "loginMember") MemberSession loginMember) {

        postService.delete(postId, loginMember);
        return "redirect:/post/list";
    }
}
