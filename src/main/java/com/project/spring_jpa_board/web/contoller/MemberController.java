package com.project.spring_jpa_board.web.contoller;

import com.project.spring_jpa_board.domain.service.MemberService;
import com.project.spring_jpa_board.web.dto.JoinDTO;
import com.project.spring_jpa_board.web.dto.LoginDTO;
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

    /**
     * GET 요청 시 빈 DTO를 모델에 담아 전달해야 타임리프 th:object와 바인딩됨
     */
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
            // "duplicateId" 같은 명시적 에러코드를 사용하면 나중에 메시지 관리가 쉬워짐
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
    public String login(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "members/loginForm";
        }

        try {
            memberService.login(loginDTO);
            // TODO: 실제 로그인 시 세션 생성 로직 필요
        } catch (IllegalArgumentException e) {
            // 필드 에러가 아닌 객체 자체의 글로벌 에러로 등록
            bindingResult.reject("loginError", e.getMessage());
            return "members/loginForm";
        }

        return "redirect:/";
    }
}
