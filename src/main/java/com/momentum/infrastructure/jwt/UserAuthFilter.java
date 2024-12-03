package com.momentum.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momentum.domain.User;
import com.momentum.global.ErrorCode;
import com.momentum.global.CommonResponse;
import com.momentum.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.momentum.config.SecurityConfig.AUTH_WHITELIST;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAuthFilter extends OncePerRequestFilter {  // OncePerRequestFilter -> 한 번 실행 보장
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // white list는 필터 거치지 않음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return Arrays.stream(AUTH_WHITELIST).anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    // 요청 권한 검증 필터
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Security Context에서 user_id 추출
            Long userIdFromToken = getUserIdFromSecurityContext();
            // 사용자 정보 검증
            User user = userService.findById(userIdFromToken);

        } catch (EntityNotFoundException e) {
            sendJsonResponse(response, CommonResponse.error(
                    ErrorCode.builder().code(1002).reason(e.getMessage()).status(HttpServletResponse.SC_NOT_FOUND).build()));
            return;

        } catch (Exception e) {
            sendJsonResponse(response, CommonResponse.error(
                    ErrorCode.builder().code(100).reason(e.getMessage()).status(HttpServletResponse.SC_FORBIDDEN).build()));
            return;
        }
        filterChain.doFilter(request, response);
    }

    public void sendJsonResponse(HttpServletResponse response, CommonResponse<Object> data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (data.getError() != null) {
            response.setStatus(data.getError().getStatus());
        }
        String json = objectMapper.writeValueAsString(data);
        response.getWriter().write(json);
    }

    private Long getUserIdFromSecurityContext() {
        // SecurityContext에서 user_id를 반환
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}