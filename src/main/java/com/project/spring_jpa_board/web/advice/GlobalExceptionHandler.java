package com.project.spring_jpa_board.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException; // 추가

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 에러: 존재하지 않는 경로/리소스를 요청했을 때 (스프링 부트 3.x 대응)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handle404(NoResourceFoundException e, Model model) {
        log.error("404 Resource Not Found: {}", e.getMessage());
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", "요청하신 페이지 또는 리소스를 찾을 수 없습니다.");
        return "error/error-page";
    }

    /**
     * 비즈니스 예외: DB에 데이터가 없을 때
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleDataNotFound(NoSuchElementException e, Model model) {
        log.error("Data Not Found: {}", e.getMessage());
        model.addAttribute("errorCode", "NOT_FOUND");
        model.addAttribute("errorMessage", "요청하신 데이터를 찾을 수 없습니다.");
        return "error/error-page";
    }

    /**
     * 500 에러: 그 외 진짜 서버 에러
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAll(Exception e, Model model) {
        log.error("Internal Server Error: ", e);
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", "서버 내부 오류가 발생했습니다.");
        return "error/error-page";
    }
}