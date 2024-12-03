package com.momentum.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momentum.global.ErrorCode;
import com.momentum.global.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static com.momentum.config.SecurityConfig.AUTH_WHITELIST;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {  // OncePerRequestFilter -> 한 번 실행 보장
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // white list는 필터 거치지 않음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return Arrays.stream(AUTH_WHITELIST).anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    // JWT 토큰 검증 필터
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Request로부터 JWT token 추출
            String token = extractTokenFromRequest(request);

            //JWT 유효성 검증
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserId(token);  // Token에서 User ID 추출
                // SecurityContext에 user_id 보관
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ServletException e) {
            sendJsonResponse(response, CommonResponse.error(
                    ErrorCode.builder().code(101).reason(e.getMessage()).status(HttpServletResponse.SC_UNAUTHORIZED).build()));
            return;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            sendJsonResponse(response, CommonResponse.error(
                    ErrorCode.builder().code(1004).reason("Invalid JWT Token").status(HttpServletResponse.SC_UNAUTHORIZED).build()));
            return;
        } catch (ExpiredJwtException e) {
            sendJsonResponse(response, CommonResponse.error(
                    ErrorCode.builder().code(1004).reason("Expired JWT Token").status(HttpServletResponse.SC_UNAUTHORIZED).build()));
            return;
        } catch (UnsupportedJwtException e) {
            sendJsonResponse(response, CommonResponse.error(
                    ErrorCode.builder().code(1004).reason("Unsupported JWT Token").status(HttpServletResponse.SC_UNAUTHORIZED).build()));
            return;
        } catch (IllegalArgumentException e) {
            sendJsonResponse(response, CommonResponse.error(
                    ErrorCode.builder().code(1004).reason("JWT claims string is empty.").status(HttpServletResponse.SC_UNAUTHORIZED).build()));
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
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(data);

        response.getWriter().write(json);
    }

    // Request 로부터 JWT token 추출
    public String extractTokenFromRequest(HttpServletRequest request) throws ServletException {
        String header = request.getHeader("Authorization");
        if (header == null)
            throw new ServletException("Authorization 헤더가 존재하지 않습니다");
        //JWT가 헤더에 있는 경우 & request header prefix가 Bearer인 경우
        else if (header.startsWith("Bearer ")) {
            return header.substring(7);  // 7 -> "Bearer " 문자 길이
        } else
            throw new ServletException("Bearer 헤더가 존재하지 않습니다");
    }
}
