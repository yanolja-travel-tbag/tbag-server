package com.tbag.tbag_backend.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.tbag.tbag_backend.exception.dto.ErrorLogging;
import com.tbag.tbag_backend.exception.dto.ErrorResponse;
import com.tbag.tbag_backend.exception.dto.ServerErrorResonse;
import com.tbag.tbag_backend.log.ApiReqResAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.slack.api.webhook.WebhookPayloads.payload;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


@Slf4j
@RestControllerAdvice
@Profile("prod")
public class GlobalExceptionHandler {

    private final Slack slackClient = Slack.getInstance();

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) throws IOException {
        if (e.getErrorCode().getHttpStatus().equals(INTERNAL_SERVER_ERROR)) {
            sendSlackAlertErrorLog(e, request);
        } else {
            getErrorContents(e, request);
        }

        return ErrorResponse.toResponseEntity(e.getErrorCode(), e.getValue());
    }

    @ExceptionHandler(value = {
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity validationError(BindException e) {
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]\n");
        }
        return ResponseEntity.badRequest().body(builder.toString());
    }

    // 봇이 돌리는 Method Not Allowed Error 슬랙 웹훅 방지용
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ServerErrorResonse> handleMethodNotAllowed(Exception e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ServerErrorResonse.builder()
                        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                        .message(request.getRequestURL() + " " + e.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerErrorResonse> unhandledException(
        Exception e, HttpServletRequest request
    ) {

        sendSlackAlertErrorLog(e, request);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ServerErrorResonse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(e.getMessage())
                        .build()
                );
    }

    private String getStackTraceAsString(StackTraceElement[] stackTrace) {
        StringBuilder stackTraceString = new StringBuilder();

        for (int i = 0; i < Math.min(4, stackTrace.length); i++) {
            StackTraceElement element = stackTrace[i];
            stackTraceString.append("\t").append(element.toString()).append("\n");
        }

        return stackTraceString.toString();
    }

    private void sendSlackAlertErrorLog(Exception e, HttpServletRequest request) {
        try {
            slackClient.send(webhookUrl, payload(p -> {
                        try {
                            return p
                                    .text("[TBAG 500 SERVER ERROR] 빠른 확인 요망")
                                    .attachments(
                                            List.of(generateSlackAttachment(e, request))
                                    );
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
            ));

        } catch (IOException slackError) {
            log.debug("Slack Web Hook 예외 발생", slackError.getMessage());
        }
    }

    private Attachment generateSlackAttachment(Exception e, HttpServletRequest request) throws IOException {
        String requestTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());

        return Attachment.builder()
                .color("ff0000")
                .title(requestTime + " 발생 에러 로그")
                .fields(getErrorContents(e, request))
                .build();
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }

    private List getErrorContents(Exception e, HttpServletRequest request) throws IOException {
        String xffHeader = request.getHeader("X-FORWARDED-FOR");  // 프록시 서버일 경우 client IP는 여기에 담길 수 있습니다.

        String traceId = (String) request.getAttribute("traceId");

        String deviceType = request.getHeader("x-custom-device-type");

        Object requestBody;
        try {
            requestBody = new ObjectMapper().readTree(request.getInputStream().readAllBytes());
        } catch (JsonParseException ex) {
            requestBody = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }

        Map<String, Object> params = ApiReqResAspect.getParams(request);
        StackTraceElement[] stackTrace = e.getStackTrace();

        List errorInfo = List.of(
                generateSlackField("Trace Id", traceId),
                generateSlackField("Device Type", deviceType),
                generateSlackField("Request Body", requestBody.toString()),
                generateSlackField("Params", params.toString()),
                generateSlackField("Request IP", xffHeader == null ? request.getRemoteAddr() : xffHeader),
                generateSlackField("Request URL", request.getRequestURL() + " " + request.getMethod()),
                generateSlackField("Error Message", e.getMessage()),
                generateSlackField("Error StackTrace", getStackTraceAsString(stackTrace))
        );


        ObjectMapper objectMapper = new ObjectMapper();
        log.info(objectMapper.writeValueAsString(
                        new ErrorLogging(
                                traceId,
                                deviceType,
                                requestBody.toString(),
                                params.toString(),
                                xffHeader == null ? request.getRemoteAddr() : xffHeader,
                                request.getRequestURL() + " " + request.getMethod(),
                                e.getMessage(),
                                getStackTraceAsString(stackTrace)
                        )
                )
        );

        return errorInfo;
    }
}
