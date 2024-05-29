package com.tbag.tbag_backend.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private String value;

    public CustomException(ErrorCode errorCode, String value) {
        this.errorCode = errorCode;
        this.value = value;
    }
}
