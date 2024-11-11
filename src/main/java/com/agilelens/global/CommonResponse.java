package com.agilelens.global;

import lombok.Builder;
import lombok.Getter;
@Getter
public class CommonResponse<T> {
    private final boolean success;
    private final T response;
    private final Error error;

    @Builder
    public CommonResponse(boolean success, T response, Error error) {
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

}
