package com.tbag.tbag_backend.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tbag.tbag_backend.exception.dto.ErrorResponse;
import com.tbag.tbag_backend.log.ApiReqResAspect;
import com.tbag.tbag_backend.log.ReqResLogging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Profile("prod")
public class GlobalExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {

            String traceId = (String) request.getAttribute("traceId");

            String xffHeader = request.getHeader("X-FORWARDED-FOR");

            String deviceType = request.getHeader("x-custom-device-type");
            String serverIp = xffHeader == null ? request.getRemoteAddr() : xffHeader;

            Object requestBody;
            try {
                requestBody = new ObjectMapper().readTree(request.getInputStream().readAllBytes());
            } catch (JsonParseException ex) {
                requestBody = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
            Map<String, Object> params = ApiReqResAspect.getParams(request);

            long start = System.currentTimeMillis(); // 4.
            long elapsedTime = System.currentTimeMillis() - start;
            String elapsedTimeStr = "execution time: " + elapsedTime + "ms";

            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            var errorResponse = ErrorResponse.toResponseEntity(e.getErrorCode(), e.getValue()).getBody();

            String jwtToken = null;

            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwtToken = authorizationHeader.substring(7); // "Bearer " 이후의 부분이 실제 토큰
            }

            ReqResLogging reqResLogging = new ReqResLogging(
                    traceId,
                    request.getMethod(),
                    String.valueOf(request.getRequestURL()),
                    params,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    serverIp,
                    deviceType,
                    requestBody,
                    errorResponse,
                    elapsedTimeStr,
                    jwtToken
            );

            try {
                String json = objectMapper.writeValueAsString(errorResponse);

                response.setStatus(errorResponse.getStatus());
                response.getWriter().write(json);
                log.info(objectMapper.writeValueAsString(reqResLogging));

//                response.getWriter().write(json);
            } catch (IOException er) {
                er.printStackTrace();
            }

        }
    }
}
