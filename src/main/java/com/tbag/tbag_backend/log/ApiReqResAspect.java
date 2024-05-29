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
    private ObjectMapper objectMapper;

    public ApiReqResAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("within(com.tbag.tbag_backend.domain.*.controller..*)")
    public void apiRestPointCut() {
    }

    @Around("apiRestPointCut()")
    public Object reqResLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String xffHeader = request.getHeader("X-FORWARDED-FOR");

        String traceId = (String) request.getAttribute("traceId");

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> params = getParams(request);

        String deviceType = request.getHeader("x-custom-device-type");
        String serverIp = xffHeader == null ? request.getRemoteAddr() : xffHeader;

        Object requestBody;
        try {
            requestBody = new ObjectMapper().readTree(request.getInputStream().readAllBytes());
        } catch (JsonParseException e) {
            requestBody = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }

        ReqResLogging reqResLogging = new ReqResLogging(
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

        if (reqResLogging.getMethod().equals("login") || reqResLogging.getMethod().equals("signup")) {
            JsonNode requestBodyJson = (JsonNode) reqResLogging.getRequestBody();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> modifiedRequestBody = objectMapper.convertValue(requestBodyJson, Map.class);

            if (modifiedRequestBody.containsKey("password")) {
                modifiedRequestBody.put("password", "");
                reqResLogging.setRequestBody(modifiedRequestBody);
            }
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            String elapsedTimeStr = "Method: " + className + "." + methodName + "() execution time: " + elapsedTime + "ms";

            ReqResLogging logging;
            logging = null;

            if (reqResLogging.getUri().equals("/auth/tokenRefresh") || reqResLogging.getUri().equals("/auth/logout")) {

                String jwtToken = null;

                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    jwtToken = authorizationHeader.substring(7); // "Bearer " 이후의 부분이 실제 토큰
                }

                logging = new ReqResLogging(
                        traceId,
                        request.getMethod(),
                        String.valueOf(request.getRequestURL()),
                        params,
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                        serverIp,
                        deviceType,
                        requestBody,
                        result,
                        elapsedTimeStr,
                        jwtToken
                );

                log.info("SUCCESS" + " " + objectMapper.writeValueAsString(logging));
            }


            return result;
        } catch (CustomException e) {
            // 에러 처리하는 로깅
            long elapsedTime = System.currentTimeMillis() - start;
            String elapsedTimeStr = "Method: " + className + "." + methodName + "() execution time: " + elapsedTime + "ms";
            ReqResLogging logging;
            logging = new ReqResLogging(
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

            log.info("ERROR" + " " + objectMapper.writeValueAsString(logging)); // 7.
            throw e;
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - start;
            String elapsedTimeStr = "Method: " + className + "." + methodName + "() execution time: " + elapsedTime + "ms";
            log.info("ERROR" + " " + objectMapper.writeValueAsString(
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
                                    new CustomException(SERVER_ERROR,
                                            "traceId : " + traceId +
                                                    ", 서버에 일시적인 장애가 있습니다."),
                                    elapsedTimeStr
                            )
                    )
            );
            throw e;
        }
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
