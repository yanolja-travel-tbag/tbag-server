package com.tbag.tbag_backend.log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbag.tbag_backend.exception.CustomException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.tbag.tbag_backend.exception.ErrorCode.SERVER_ERROR;


@Component
@Aspect
public class ApiReqResAspect {
    private static final Logger log = LoggerFactory.getLogger(ApiReqResAspect.class);
    private final ObjectMapper objectMapper;

    public ApiReqResAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("within(com.tbag.tbag_backend.domain.*.controller..*)")
    public void apiRestPointCut() {
    }

    @Around("apiRestPointCut()")
    public Object reqResLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String traceId = (String) request.getAttribute("traceId");
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> params = getParams(request);
        String serverIp = getServerIp(request);
        String deviceType = request.getHeader("x-custom-device-type");
        Object requestBody = getRequestBody(request);

        ReqResLogging reqResLogging = createReqResLogging(request, traceId, className, methodName, params, serverIp, deviceType, requestBody);

        if (isSensitiveMethod(methodName)) {
            maskSensitiveData(reqResLogging);
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            logSuccess(request, reqResLogging, className, methodName, start, result);
            return result;
        } catch (CustomException e) {
            logCustomException(reqResLogging, className, methodName, start, e);
            throw e;
        } catch (Exception e) {
            logException(reqResLogging, className, methodName, start, traceId, e);
            throw e;
        }
    }

    private String getServerIp(HttpServletRequest request) {
        String xffHeader = request.getHeader("X-FORWARDED-FOR");
        return xffHeader == null ? request.getRemoteAddr() : xffHeader;
    }

    private Object getRequestBody(HttpServletRequest request) throws IOException {
        try {
            return new ObjectMapper().readTree(request.getInputStream().readAllBytes());
        } catch (JsonParseException e) {
            return new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private ReqResLogging createReqResLogging(HttpServletRequest request, String traceId, String className, String methodName, Map<String, Object> params, String serverIp, String deviceType, Object requestBody) {
        return new ReqResLogging(
                traceId,
                className,
                request.getMethod(),
                request.getRequestURI(),
                methodName,
                params,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                serverIp,
                deviceType,
                requestBody
        );
    }

    private boolean isSensitiveMethod(String methodName) {
        return methodName.equals("login") || methodName.equals("signup");
    }

    private void maskSensitiveData(ReqResLogging reqResLogging) {
        JsonNode requestBodyJson = (JsonNode) reqResLogging.getRequestBody();
        Map<String, Object> modifiedRequestBody = objectMapper.convertValue(requestBodyJson, Map.class);
        if (modifiedRequestBody.containsKey("password")) {
            modifiedRequestBody.put("password", "");
            reqResLogging.setRequestBody(modifiedRequestBody);
        }
    }

    private void logSuccess(HttpServletRequest request, ReqResLogging reqResLogging, String className, String methodName, long start, Object result) throws IOException {
        long elapsedTime = System.currentTimeMillis() - start;
        String elapsedTimeStr = "Method: " + className + "." + methodName + "() execution time: " + elapsedTime + "ms";

        if (isAuthRelatedUri(reqResLogging.getUri())) {
            log.info("SUCCESS " + objectMapper.writeValueAsString(createAuthLogging(reqResLogging, result, elapsedTimeStr)));
        } else {
            log.info("SUCCESS " + objectMapper.writeValueAsString(reqResLogging));
        }
    }

    private boolean isAuthRelatedUri(String uri) {
        return uri.equals("/auth/tokenRefresh") || uri.equals("/auth/logout");
    }

    private ReqResLogging createAuthLogging(ReqResLogging reqResLogging, Object result, String elapsedTimeStr) {
        return new ReqResLogging(
                reqResLogging.getTraceId(),
                reqResLogging.getHttpMethod(),
                reqResLogging.getUri(),
                reqResLogging.getParams(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                reqResLogging.getServerIp(),
                reqResLogging.getDeviceType(),
                reqResLogging.getRequestBody(),
                result,
                elapsedTimeStr
        );
    }

    private void logCustomException(ReqResLogging reqResLogging, String className, String methodName, long start, CustomException e) throws IOException {
        long elapsedTime = System.currentTimeMillis() - start;
        String elapsedTimeStr = "Method: " + className + "." + methodName + "() execution time: " + elapsedTime + "ms";
        ReqResLogging logging = new ReqResLogging(
                reqResLogging.getTraceId(),
                reqResLogging.getClassName(),
                reqResLogging.getHttpMethod(),
                reqResLogging.getUri(),
                reqResLogging.getMethod(),
                reqResLogging.getParams(),
                reqResLogging.getLogTime(),
                reqResLogging.getServerIp(),
                reqResLogging.getDeviceType(),
                reqResLogging.getRequestBody(),
                e.getErrorCode().getHttpStatus().toString() + e.getErrorCode() + " " + e.getValue(),
                elapsedTimeStr
        );
        log.info("ERROR " + objectMapper.writeValueAsString(logging));
    }

    private void logException(ReqResLogging reqResLogging, String className, String methodName, long start, String traceId, Exception e) throws IOException {
        long elapsedTime = System.currentTimeMillis() - start;
        String elapsedTimeStr = "Method: " + className + "." + methodName + "() execution time: " + elapsedTime + "ms";
        log.info("ERROR " + objectMapper.writeValueAsString(
                new ReqResLogging(
                        reqResLogging.getTraceId(),
                        reqResLogging.getClassName(),
                        reqResLogging.getHttpMethod(),
                        reqResLogging.getUri(),
                        reqResLogging.getMethod(),
                        reqResLogging.getParams(),
                        reqResLogging.getLogTime(),
                        reqResLogging.getServerIp(),
                        reqResLogging.getDeviceType(),
                        reqResLogging.getRequestBody(),
                        new CustomException(SERVER_ERROR, "traceId : " + traceId + ", 서버에 일시적인 장애가 있습니다."),
                        elapsedTimeStr
                )
        ));
    }

    public static Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, Object> jsonObject = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String replaceParam = paramName.replace("\\.", "-");
            jsonObject.put(replaceParam, request.getParameter(paramName));
        }
        return jsonObject;
    }
}
