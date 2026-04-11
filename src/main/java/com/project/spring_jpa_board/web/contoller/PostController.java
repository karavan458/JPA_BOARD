package com.project.spring_jpa_board.web.contoller;

import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.domain.service.PostService;
import com.project.spring_jpa_board.web.dto.member.SessionDTO;
import com.project.spring_jpa_board.web.dto.post.PostDetailDTO;
import com.project.spring_jpa_board.web.dto.post.PostSaveDTO;
import com.project.spring_jpa_board.web.dto.post.PostSearchCondition;
import com.project.spring_jpa_board.web.dto.post.PostUpdateDTO;
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

    @GetMapping("/new")
    public String createForm(Model model,
            @SessionAttribute(name = "loginMember", required = false) SessionDTO loginMember) {

        if(loginMember == null) {
            return "redirect:/";
        }

        model.addAttribute("postForm", new PostSaveDTO());
        return "post/createForm";
    }

    @PostMapping("/new")
    public String create(@Validated @ModelAttribute("postForm") PostSaveDTO postSaveDTO, BindingResult bindingResult,
            @SessionAttribute(name = "loginMember", required = false) SessionDTO loginMember) {

        if(bindingResult.hasErrors()) {
            log.info("errors : {}", bindingResult.getAllErrors());
            return "post/createForm";
        }

        if(loginMember == null) {
            return "redirect:/";
        }

        postService.savePost(loginMember.getId(), postSaveDTO);
        return "redirect:/post/list";
    }

    @GetMapping("/{postId}")
    public String read(@PathVariable("postId") Long postId,
             @SessionAttribute(name = "loginMember", required = false) SessionDTO loginMember, Model model) {

        Post post = postService.findById(postId);
        model.addAttribute("post", PostDetailDTO.from(post));
        model.addAttribute("loginMember", loginMember);
        return "post/detail";
    }

    @GetMapping("/list")
    public String list(
            @ModelAttribute("postSearch") PostSearchCondition condition,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        log.info("검색 조건 복구 확인: title={}, writer={}", condition.getTitle(), condition.getWriter());
        Page<Post> page = postService.search(condition, pageable);

        log.info("조회된 게시글 수 : {}", page.getNumberOfElements());
        for (Post post : page) {
            log.info("게시글 제목 : {}, 작성사 : {}", post.getTitle(), post.getMember().getName());
        }

        model.addAttribute("page", page);
        return "post/list";
    }

    @GetMapping("/{postId}/update")
    public String updateForm(@PathVariable("postId") Long postId, Model model) {
        Post post = postService.findById(postId);

        PostUpdateDTO postUpdateDTO = new PostUpdateDTO(post.getTitle(), post.getContent());
        model.addAttribute("postId", postId);
        model.addAttribute("postUpdateDTO", postUpdateDTO);
        return "post/updateForm";
    }

    @PostMapping("/{postId}/update")
    public String update(@PathVariable("postId") Long postId,
            @Valid @ModelAttribute PostUpdateDTO postUpdateDTO,
            BindingResult bindingResult,
            @SessionAttribute(name = "loginMember", required = false) SessionDTO loginMember,
            @ModelAttribute("postSearch") PostSearchCondition condition,
            @PageableDefault Pageable pageable,
            RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            return "post/updateForm";
        }

        if(loginMember == null) {
            return "redirect:/";
        }

        postService.update(postId, postUpdateDTO, loginMember);

        redirectAttributes.addAttribute("page", pageable.getPageNumber());
        redirectAttributes.addAttribute("title", condition.getTitle());
        redirectAttributes.addAttribute("writer", condition.getWriter());

        return "redirect:/post/list";
    }

    @PostMapping("/{postId}/delete")
    public String delete(@PathVariable("postId") Long postId,
            @SessionAttribute(name = "loginMember", required = false) SessionDTO loginMember) {

        if(loginMember == null) {
            return "redirect:/";
        }

        postService.delete(postId, loginMember);
        return "redirect:/post/list";
    }
}
