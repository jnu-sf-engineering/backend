package com.momentum.config;

import com.momentum.infrastructure.jwt.JwtAuthFilter;
import com.momentum.infrastructure.jwt.JwtUtil;
import com.momentum.infrastructure.jwt.UserAuthFilter;
import com.momentum.service.CardService;
import com.momentum.service.ProjectService;
import com.momentum.service.SprintService;
import com.momentum.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final CardService cardService;
    private final SprintService sprintService;
    private final ProjectService projectService;
    private final UserService userService;

    public static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**", "/api-docs", "/swagger-ui-custom.html",
            "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html"
    };

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // JWT 필터 생성
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtil);
        // User Auth 필터 생성
        UserAuthFilter userAuthFilter = new UserAuthFilter(cardService, sprintService, projectService, userService);

        // CSRF, CORS
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        // 세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));

        // FormLogin, Http Basic 인증 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 인증 처리 규칙 설정
        http.authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(AUTH_WHITELIST).permitAll()  // white list는 인증 필요 없음
                    .anyRequest().authenticated());  // white list를 제외한 경로 인증 설정

        // 권한 인증 필터 추가
        http.addFilterBefore(userAuthFilter, UsernamePasswordAuthenticationFilter.class)  // user_id 일치 검증 필터
            .addFilterBefore(jwtAuthFilter, UserAuthFilter.class);  // JWT 유효성 검증 필터
        return http.build();
    }
}
