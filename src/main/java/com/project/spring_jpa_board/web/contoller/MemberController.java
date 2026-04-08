package com.project.spring_jpa_board.web.contoller;

import com.project.spring_jpa_board.domain.entity.Member;
import com.project.spring_jpa_board.domain.service.MemberService;
import com.project.spring_jpa_board.web.dto.JoinDTO;
import com.project.spring_jpa_board.web.dto.LoginDTO;
import com.project.spring_jpa_board.web.dto.SessionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String join(@Valid @ModelAttribute("joinDTO") JoinDTO joinDTO, BindingResult bindingResult) {
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
    public String login(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO, BindingResult bindingResult, HttpServletRequest request) {
        if(bindingResult.hasErrors()) {
            return "members/loginForm";
        }

        try {
            Member loginMember = memberService.login(loginDTO);
            HttpSession session = request.getSession();

            SessionDTO sessionDTO = new SessionDTO(loginMember.getLoginId(), loginMember.getName(), loginMember.getEmail());
            session.setAttribute("loginMember", sessionDTO);
        } catch (IllegalArgumentException e) {
            // 필드 에러가 아닌 객체 자체의 글로벌 에러로 등록
            bindingResult.reject("loginError", e.getMessage());
            return "members/loginForm";
        }

        return "redirect:/";
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
