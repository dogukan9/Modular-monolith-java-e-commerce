package com.shopwise.shared.api;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse <T>{

    private final boolean success;
    private final String message;
    private final T data;
    private final String errorCode;

    public static <T> ApiResponse<T> success(T data,String message){
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }


    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
}
