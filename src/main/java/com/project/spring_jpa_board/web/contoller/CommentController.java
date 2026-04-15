package com.project.spring_jpa_board.web.contoller;

import com.project.spring_jpa_board.domain.service.CommentService;
import com.project.spring_jpa_board.web.dto.comment.CommentSaveRequest;
import com.project.spring_jpa_board.web.dto.member.MemberSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/save")
    public String save(
            @PathVariable Long postId,
            @Validated @ModelAttribute CommentSaveRequest request,
            BindingResult bindingResult,
            @SessionAttribute(name = "loginMember") MemberSession loginMember) {

        if(bindingResult.hasErrors()) {
            log.info("검증 오류 발생 = {}", bindingResult.getAllErrors());
            return "redirect:/post/" + postId;
        }

        commentService.saveComment(loginMember.getId(), postId, request);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/{postId}/{commentId}/delete")
    public String delete(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @SessionAttribute(name = "loginMember") MemberSession loginMember) {

        commentService.delete(commentId, loginMember.getId());
        return "redirect:/post/" + postId;
    }
}
