package com.tbag.tbag_backend.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ReqResLogging {

    @JsonProperty(value = "traceId")
    private String traceId;

    @JsonProperty(value = "class")
    private String className;

    @JsonProperty(value = "http_method")
    private String httpMethod;

    @JsonProperty(value = "uri")
    private String uri;

    @JsonProperty(value = "method")
    private String method;

    @JsonProperty(value = "params")
    private Map<String, Object> params;

    @JsonProperty(value = "log_time")
    private String logTime;

    @JsonProperty(value = "server_ip")
    private String serverIp;

    @JsonProperty(value = "device_type")
    private String deviceType;

    @JsonProperty(value = "request_body")
    private Object requestBody;

    @JsonProperty(value = "response_body")
    private Object responseBody;

    @JsonProperty(value = "elapsed_time")
    private String elapsedTime;

    @JsonProperty(value = "jwtToken")
    private String jwtToken;

    public ReqResLogging(String traceId, String className, String httpMethod, String uri, String method,
                         Map<String, Object> params, String logTime, String serverIp, String deviceType, Object requestBody
    ) {
        this.traceId = traceId;
        this.className = className;
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.method = method;
        this.params = params;
        this.logTime = logTime;
        this.serverIp = serverIp;
        this.deviceType = deviceType;
        this.requestBody = requestBody;
    }

    public ReqResLogging(String traceId, String className, String httpMethod, String uri, String method,
                         Map<String, Object> params, String logTime, String serverIp, String deviceType,
                         Object requestBody, Object responseBody, String elapsedTime) {
        this.traceId = traceId;
        this.className = className;
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.method = method;
        this.params = params;
        this.logTime = logTime;
        this.serverIp = serverIp;
        this.deviceType = deviceType;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.elapsedTime = elapsedTime;
    }

    public ReqResLogging(String traceId, String httpMethod, String uri,
                         Map<String, Object> params,
                         String logTime, String serverIp, String deviceType,
                         Object requestBody, Object responseBody, String elapsedTime, String jwtToken) {
        this.traceId = traceId;
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.params = params;
        this.logTime = logTime;
        this.serverIp = serverIp;
        this.deviceType = deviceType;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.elapsedTime = elapsedTime;
        this.jwtToken = jwtToken;
    }

}
