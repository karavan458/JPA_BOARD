package com.project.spring_jpa_board.web.contoller;

import com.project.spring_jpa_board.web.dto.member.MemberSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {

        HttpSession session = request.getSession(false);
        if(session == null) {
            return "index";
        }

        MemberSession sessionDTO = (MemberSession) session.getAttribute("loginMember");
        if(sessionDTO == null) {
            return "index";
        }

        model.addAttribute("loginMember", sessionDTO);
        return "home";
    }
}
