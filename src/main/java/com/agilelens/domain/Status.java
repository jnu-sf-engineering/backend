package com.agilelens.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static Status from(String value) {
        String v = value.toUpperCase();

        if (v.equals("TO_DO"))
            return Status.TO_DO;
        else if (v.equals("IN_PROGRESS"))
            return Status.IN_PROGRESS;
        else if (v.equals("DONE"))
            return Status.DONE;
        else
            throw new IllegalArgumentException("'"+value+"'는 허용한 상태값 이외의 값입니다.");
    }
}