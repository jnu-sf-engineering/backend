package com.momentum.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {  // OncePerRequestFilter -> 한 번 실행 보장
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper
    // JWT 토큰 검증 필터
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Request로부터 JWT token 추출
        String token = extractTokenFromRequest(request);

        if (token != null) {
            //JWT 유효성 검증
            try {
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserId(token);  // Token에서 User ID 추출
                    // SecurityContext에 user_id 보관
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.emptyList()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                throw new RuntimeException("Invalid JWT Token", e);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                throw new RuntimeException("Expired JWT Token", e);
            } catch (UnsupportedJwtException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                throw new RuntimeException("Unsupported JWT Token", e);
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                throw new RuntimeException("JWT claims string is empty.", e);
            }
        } else
            return;

        filterChain.doFilter(request, response);
    }

    // Request 로부터 JWT token 추출
    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        //JWT가 헤더에 있는 경우 & request header prefix가 Bearer인 경우
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);  // 7 -> "Bearer " 문자 길이
        }
        return null;
    }
}
