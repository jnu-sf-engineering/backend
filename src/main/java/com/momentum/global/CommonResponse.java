package com.momentum.global;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommonResponse<T> {
    private final boolean success;
    private final T response;
    private final ErrorCode error;

    @Builder
    public CommonResponse(boolean success, T response, ErrorCode error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }

    public static <T> CommonResponse<T> success() {
        return (CommonResponse<T>) CommonResponse.builder()
                .success(true).response(null).error(null).build();
    }
    public static <T> CommonResponse<T> success(T data) {
        return (CommonResponse<T>) CommonResponse.builder()
                .success(true).response(data).error(null).build();
    }

    public static <T> CommonResponse<T> error(ErrorCode error) {
        return (CommonResponse<T>) CommonResponse.builder()
                .success(false).response(null).error(error).build();
    }

}
