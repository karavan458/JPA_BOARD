package com.project.spring_jpa_board.web.advice;

import com.project.spring_jpa_board.web.dto.member.MemberSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

@ControllerAdvice
public class GlobalAdvice {

    @ModelAttribute
    public void addLoginMember(
            @SessionAttribute(name = "loginMember", required = false) MemberSession loginMember,
            Model model) {

        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
        }
    }
}
