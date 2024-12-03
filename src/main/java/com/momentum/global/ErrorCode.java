package com.momentum.global;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorCode {
    private static final String DEFAULT_MESSAGE = "일시적으로 접속이 원활하지 않습니다.";

    private final int code;
    private final String reason;
    private final int status;

    public ErrorCode() {
        this.code = 0;
        this.reason = DEFAULT_MESSAGE;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    @Builder
    public ErrorCode(int code, String reason, int status) {
        this.code = code;
        this.reason = reason;
        this.status = status;
    }
}
