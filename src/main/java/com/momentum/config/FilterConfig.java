package com.momentum.config;

import com.momentum.infrastructure.jwt.JwtAuthFilter;
import com.momentum.infrastructure.jwt.JwtUtil;
import com.momentum.infrastructure.jwt.UserAuthFilter;
import com.momentum.service.CardService;
import com.momentum.service.ProjectService;
import com.momentum.service.SprintService;
import com.momentum.service.UserService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class FilterConfig implements WebMvcConfigurer {
    private final JwtUtil jwtUtil;
    private final CardService cardService;
    private final SprintService sprintService;
    private final ProjectService projectService;
    private final UserService userService;

    @Bean
    public FilterRegistrationBean<Filter> jwtAuthFilter() {
        return new FilterRegistrationBean<Filter>(new JwtAuthFilter(jwtUtil));
    }

    @Bean
    public FilterRegistrationBean<Filter> userAuthFilter() {
        return new FilterRegistrationBean<Filter>(new UserAuthFilter(cardService, sprintService, projectService, userService));
    }
}
