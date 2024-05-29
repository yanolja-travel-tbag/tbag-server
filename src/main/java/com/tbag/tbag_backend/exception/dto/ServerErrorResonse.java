package com.tbag.tbag_backend.exception.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ServerErrorResonse {
    private final int status;

    private final String message;

    @Builder
    public ServerErrorResonse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
