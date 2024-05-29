package com.tbag.tbag_backend.domain.Auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {

        String requestTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
        String xffHeader = request.getHeader("X-FORWARDED-FOR");

        String traceId = (String) request.getAttribute("traceId");

        String deviceType = request.getHeader("x-custom-device-type");
        Object requestBody = new ObjectMapper().readTree(request.getInputStream().readAllBytes());

        log.error("===OAUTH2 FAIL=== " +
                "Trace Id : " + traceId +
                ", Exception : " + authenticationException.getLocalizedMessage() +
                ", Request Time : " + requestTime +
                ", Ip : " +  ( xffHeader == null ? request.getRemoteAddr() : xffHeader ) +
                ", Device Type : " + deviceType +
                ", Request Body : " + requestBody);

        String encodedErrorMessage = URLEncoder.encode(authenticationException.getLocalizedMessage(), StandardCharsets.UTF_8);
        String targetUrl = "/oauth2/redirect?error=" + encodedErrorMessage;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
