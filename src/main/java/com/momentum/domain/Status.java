package com.momentum.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Status {
    TO_DO(0),
    IN_PROGRESS(1),
    DONE(2);

    private final int value;
    Status(int value) {
        this.value = value;
    }

    @JsonValue
    public String getStatusString() {
        // 직렬화할 때 상태를 소문자로 변환하여 반환
        return name().toLowerCase();
    }

    @JsonCreator
    public static Status from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status 값은 null일 수 없습니다.");
        }
        return switch (value.toUpperCase()) {
            case "TO_DO", "0" -> TO_DO;
            case "IN_PROGRESS", "1" -> IN_PROGRESS;
            case "DONE", "2" -> DONE;
            default -> throw new IllegalArgumentException("'" + value + "'는 허용된 상태 값이 아닙니다.");
        };
    }
}