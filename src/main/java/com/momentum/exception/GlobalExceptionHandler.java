package com.momentum.exception;

import com.momentum.global.CommonResponse;
import com.momentum.global.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    public static int code = 0;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponse<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        code = 2;

        // 스택 트레이스에서 발생 위치 추출
        for (StackTraceElement element : ex.getStackTrace()) {
            String className = element.getClassName(); // 예외가 발생한 클래스의 전체 이름
            if (className.contains("Project")) {
                code = 2002;
                break;
            } else if (className.contains("Sprint")) {
                code = 3002;
                break;
            } else if (className.contains("Card")) {
                code = 4002;
                break;
            }
        }
        return ResponseEntity.badRequest().body(CommonResponse.error(
                        ErrorCode.builder()
                                .code(code).reason(ex.getMessage()).status(HttpStatus.NOT_FOUND.value())
                                .build()
                )
        );
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<CommonResponse<Object>> handleIllegalAccessException(IllegalAccessException ex) {
        return ResponseEntity.badRequest().body(CommonResponse.error(
                        ErrorCode.builder()
                                .code(1004).reason(ex.getMessage()).status(HttpStatus.UNAUTHORIZED.value())
                                .build()
                )
        );
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<CommonResponse<Void>> handleDateTimeParseException(DateTimeParseException ex) {
        return ResponseEntity.badRequest().body(CommonResponse.error(
                ErrorCode.builder()
                        .code(3003).reason("잘못된 날짜 형식입니다.").status(HttpStatus.BAD_REQUEST.value())
                        .build()
                )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResponse<Object>> handleMissingParams(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        return ResponseEntity.badRequest().body(CommonResponse.error(
                ErrorCode.builder()
                        .code(4005).reason("Request Parameter 누락입니다.(" + paramName + ")").status(HttpStatus.BAD_REQUEST.value())
                        .build()
                )
        );
    }
}

