package com.project.spring_jpa_board.web.contoller;

import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.service.MemberService;
import com.project.spring_jpa_board.web.dto.member.JoinDTO;
import com.project.spring_jpa_board.web.dto.member.LoginDTO;
import com.project.spring_jpa_board.web.dto.member.MyPageDTO;
import com.project.spring_jpa_board.web.dto.member.SessionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("joinDTO", new JoinDTO());
        return "members/joinForm";
    }

    @PostMapping("/join")
    public String join(
            @Valid @ModelAttribute("joinDTO") JoinDTO joinDTO,
            BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "members/joinForm";
        }

        try {
            memberService.join(joinDTO);
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("loginId", "duplicateId", e.getMessage());
            return "members/joinForm";
        }

        return "redirect:/members/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "members/loginForm";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
            BindingResult bindingResult,
            @RequestParam(value = "redirectURL", defaultValue = "/") String redirectURL,
            HttpServletRequest request) {

        if(bindingResult.hasErrors()) {
            return "members/loginForm";
        }

        try {
            Member loginMember = memberService.login(loginDTO);
            HttpSession session = request.getSession();

            SessionDTO sessionDTO = new SessionDTO(loginMember.getId(), loginMember.getLoginId(), loginMember.getName(), loginMember.getEmail());
            session.setAttribute("loginMember", sessionDTO);

            return "redirect:" + redirectURL;
        } catch (IllegalArgumentException e) {

            bindingResult.reject("loginError", e.getMessage());
            return "members/loginForm";
        }
    }

    @GetMapping("/mypage")
    public String myPage(
            @SessionAttribute(name = "loginMember") SessionDTO loginMember,
            Model model) {

        MyPageDTO myPage = memberService.getMyPage(loginMember);
        model.addAttribute("myPage", myPage);

        return "members/myPage";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
