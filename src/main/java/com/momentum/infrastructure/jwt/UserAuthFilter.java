package com.momentum.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.momentum.domain.Card;
import com.momentum.domain.Project;
import com.momentum.domain.Sprint;
import com.momentum.domain.User;
import com.momentum.service.CardService;
import com.momentum.service.ProjectService;
import com.momentum.service.SprintService;
import com.momentum.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAuthFilter extends OncePerRequestFilter {  // OncePerRequestFilter -> 한 번 실행 보장
    private final CardService cardService;
    private final SprintService sprintService;
    private final ProjectService projectService;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper

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

            String requestURI = request.getRequestURI();

            // 요청 경로에 따라 처리
            if (requestURI.startsWith("/v1/cards/")) {
                Long cardId = extractPathVariable(request, 3);
                if (cardId != null && !validateCardRequest(cardId, userIdFromToken)) {
                    log.debug("카드 권한 없음");
                    throw new IllegalAccessException("해당 카드에 대한 권한이 없습니다.");
                }
            } else if (requestURI.equals("/v1/cards")) {
                Long sprintId = extractFromRequestBody(request, "sprint_id");
                if (sprintId != null && !validateSprintRequest(sprintId, userIdFromToken)) {
                    log.debug("스프린트 권한 없음");
                    throw new IllegalAccessException("해당 스프린트에 대한 권한이 없습니다.");
                }
            } else if (requestURI.startsWith("/v1/sprints/")) {
                Long sprintId = extractPathVariable(request, 3);
                if (sprintId != null && !validateSprintRequest(sprintId, userIdFromToken)) {
                    log.debug("스프린트 권한 없음");
                    throw new IllegalAccessException("해당 스프린트에 대한 권한이 없습니다.");
                }
            } else if (requestURI.equals("/v1/sprints")) {
                Long projectId = extractFromRequestBody(request, "project_id");
                if (projectId != null && !validateProjectRequest(projectId, userIdFromToken)) {
                    log.debug("프로젝트 권한 없음");
                    throw new IllegalAccessException("해당 프로젝트에 대한 권한이 없습니다.");
                }
            }
        } catch (IllegalAccessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new RuntimeException(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            throw new RuntimeException(e.getMessage(),e);
        }
        filterChain.doFilter(request, response);
    }

    private Long getUserIdFromSecurityContext() {
        // SecurityContext에서 user_id를 반환
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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

    // Request body에서 특정 필드를 추출
    private Long extractFromRequestBody(HttpServletRequest request, String field) throws IOException {
        // InputStream에서 JSON 데이터를 읽어 필드 값 추출
        Map<String, Object> bodyMap = objectMapper.readValue(request.getInputStream(), Map.class);
        if (bodyMap.containsKey(field)) {
            try {
                return Long.parseLong(bodyMap.get(field).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Request body의 " + field + " 필드를 Long 타입으로 변환할 수 없습니다.", e);
            }
        }
        return null;
    }

    // Card 요청 검증
    private boolean validateCardRequest(Long cardId, Long userIdFromToken) {
        Long userIdFromCard = fetchUserIdFromCard(cardId); // DB 조회
        return userIdFromToken.equals(userIdFromCard);
    }

    // Sprint 요청 검증
    private boolean validateSprintRequest(Long sprintId, Long userIdFromToken) {
        Long userIdFromSprint = fetchUserIdFromSprint(sprintId); // DB 조회
        return userIdFromToken.equals(userIdFromSprint);
    }

    // Project 요청 검증
    private boolean validateProjectRequest(Long projectId, Long userIdFromToken) {
        Long userIdFromProject = fetchUserIdFromProject(projectId); // DB 조회
        return userIdFromToken.equals(userIdFromProject);
    }

    private Long fetchUserIdFromCard(Long cardId) {
        Card card = cardService.findById(cardId);
        return card.getSprint().getProject().getUser().getId();
    }

    private Long fetchUserIdFromSprint(Long sprintId) {
        Sprint sprint = sprintService.findById(sprintId);
        return sprint.getProject().getUser().getId();
    }

    private Long fetchUserIdFromProject(Long projectId) {
        Project project = projectService.findById(projectId);
        return project.getUser().getId();
    }

}