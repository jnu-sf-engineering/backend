package com.momentum.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momentum.global.CommonResponse;
import com.momentum.global.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class MissingPathVariableFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        int code =0;
        try {
            // Path Variable 검증
            if (requestURI.startsWith("/v1/sprints/")) {
                code = 3001;
                Long sprintId = extractPathVariable(request, 3);
                if (sprintId == null) {
                    throw new IllegalArgumentException("Path Variable 누락입니다.(sprint_id)");
                }
            } else if (requestURI.equals("/v1/cards/move")) {
                throw new IllegalArgumentException("Path Variable 누락입니다.(card_id)");
            } else if (requestURI.startsWith("/v1/cards/")) {
                code = 4001;
                Long cardId = extractPathVariable(request, 3);
                if (cardId == null) {
                    throw new IllegalArgumentException("Path Variable 누락입니다.(card_id)");
                }
            }
        } catch (IllegalArgumentException e) {
            sendJsonResponse(response, CommonResponse.error(
                    ErrorCode.builder().code(code).reason(e.getMessage()).status(HttpServletResponse.SC_BAD_REQUEST).build()));
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

    // Path variable에서 특정 키를 추출
    public static Long extractPathVariable(HttpServletRequest request, int variableIndex) {
        String[] uriParts = request.getRequestURI().split("/");

        if (uriParts.length > variableIndex) {
            try {
                return Long.parseLong(uriParts[variableIndex]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Path variable의 " + variableIndex + "번째 인덱스가 Long 타입으로 변환할 수 없습니다.", e);
            }
        }
        return null;
    }
}
