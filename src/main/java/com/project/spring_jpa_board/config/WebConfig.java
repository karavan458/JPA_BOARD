package com.project.spring_jpa_board.config;

import com.project.spring_jpa_board.web.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/members/join",
                        "/members/login",
                        "/members/logout",
                        "/post/list",
                        "/post/{postId:[0-9]+}",
                        "/css/**",
                        "/*.ico",
                        "/error"
                );
    }
}
