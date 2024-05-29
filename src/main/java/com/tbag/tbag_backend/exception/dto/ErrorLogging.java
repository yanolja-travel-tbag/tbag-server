package com.tbag.tbag_backend.exception.dto;

import lombok.Data;

@Data
public class ErrorLogging {
    private String traceId;
    private String deviceType;
    private String requestBody;
    private String params;
    private String requestIP;
    private String requestURL;
    private String errorMessage;
    private String errorStackTrace;

    public ErrorLogging() {
    }

    public ErrorLogging(String traceId, String deviceType, String requestBody, String params,
                        String requestIP, String requestURL, String errorMessage, String errorStackTrace) {
        this.traceId = traceId;
        this.deviceType = deviceType;
        this.requestBody = requestBody;
        this.params = params;
        this.requestIP = requestIP;
        this.requestURL = requestURL;
        this.errorMessage = errorMessage;
        this.errorStackTrace = errorStackTrace;
    }
}
