package com.momentum.controller;

import com.momentum.dto.SprintCreateRequest;
import com.momentum.dto.SprintCreateResponse;
import com.momentum.dto.SprintFindResponse;
import com.momentum.dto.SprintUpdateRequest;
import com.momentum.global.ErrorCode;
import com.momentum.global.CommonResponse;
import com.momentum.service.ProjectService;
import com.momentum.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/v1/sprints")
@RestController
public class SprintController {
    private final ProjectService projectService;
    private final SprintService sprintService;

    @PostMapping()
    public ResponseEntity<CommonResponse<Object>> create(@Valid @RequestBody SprintCreateRequest request, BindingResult bindingResult,
                                                         Principal principal) throws IllegalAccessException {
        if (bindingResult.hasErrors()) {
            String errorMessage = "필드 데이터 누락입니다.("+extractErrorMessages(bindingResult)+")";
            return responseEntityWithError(3001, errorMessage, HttpStatus.BAD_REQUEST);
        }

        Long user_id = projectService.findById(request.getProject_id()).getUser().getId();
        permissionAuth(principal, user_id);

        if (request.getEnd_date().isBefore(request.getStart_date())) {
            return responseEntityWithError(3004, "잘못된 날짜 순서입니다.", HttpStatus.BAD_REQUEST);
        }
        if(sprintService.existsByProjectIdAndName(request.getProject_id(), request.getName())) {
            return responseEntityWithError(3005, "스프린트명이 중복입니다.", HttpStatus.CONFLICT);
        }

        SprintCreateResponse response = SprintCreateResponse
                .from(sprintService.save(request));
        return ResponseEntity.ok().body(CommonResponse.success(response));

    }

    @GetMapping("/{sprint_id}")
    public ResponseEntity<CommonResponse<Object>> read(@PathVariable("sprint_id") Long sprint_id,
                                                       Principal principal) throws IllegalAccessException {
        Long user_id = sprintService.findById(sprint_id).getProject().getUser().getId();
        permissionAuth(principal, user_id);

        SprintFindResponse response = SprintFindResponse
                .from(sprintService.findById(sprint_id));
        return ResponseEntity.ok().body(CommonResponse.success(response));
    }

    @PutMapping("/{sprint_id}")
    public ResponseEntity<CommonResponse<Object>> update(@PathVariable("sprint_id") Long sprint_id,
                                                         @Valid @RequestBody SprintUpdateRequest request, BindingResult bindingResult,
                                                         Principal principal) throws IllegalAccessException {
        if (bindingResult.hasErrors()) {
            String errorMessage = "필드 데이터 누락입니다.("+extractErrorMessages(bindingResult)+")";
            return responseEntityWithError(3001, errorMessage, HttpStatus.BAD_REQUEST);
        }

        Long user_id = sprintService.findById(sprint_id).getProject().getUser().getId();
        permissionAuth(principal, user_id);

        if (request.getEnd_date().isBefore(request.getStart_date())) {
            return responseEntityWithError(3004, "잘못된 날짜 순서입니다.", HttpStatus.BAD_REQUEST);
        }
        Long project_id = sprintService.findById(sprint_id).getProject().getId();
        if(sprintService.existsByProjectIdAndName(project_id, request.getName())) {
            return responseEntityWithError(3005, "스프린트명이 중복입니다.", HttpStatus.CONFLICT);
        }

        SprintCreateResponse response = SprintCreateResponse
                .from(sprintService.update(sprint_id, request));
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @DeleteMapping("/{sprint_id}")
    public ResponseEntity<CommonResponse<Object>> delete(@PathVariable("sprint_id") Long sprint_id,
                                                         Principal principal) throws IllegalAccessException {
        Long user_id = sprintService.findById(sprint_id).getProject().getUser().getId();
        permissionAuth(principal, user_id);

        sprintService.delete(sprint_id);
        return ResponseEntity.ok(CommonResponse.success());
    }


    /*
    * Helper Functions Down Here.
    * */

    // 접근 권한 인증
    private void permissionAuth(Principal principal, Long user_id) throws IllegalAccessException {
        if (!principal.getName().equals(String.valueOf(user_id))) {
            throw new IllegalAccessException("요청 권한이 없습니다.");
        }
    }

    // error 내용을 담은 ResponseEntity 생성
    private ResponseEntity<CommonResponse<Object>> responseEntityWithError(int code, String errorMessage, HttpStatus status) {
        return ResponseEntity.status(status).body(
                CommonResponse.error(ErrorCode.builder()
                        .code(code).reason(errorMessage).status(status.value()).build())
        );
    }

    // 에러 메시지를 추출하는 유틸리티 함수
    private String extractErrorMessages(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}
