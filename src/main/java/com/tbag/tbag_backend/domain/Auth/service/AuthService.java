package com.tbag.tbag_backend.domain.Auth.service;

import com.tbag.tbag_backend.domain.Auth.dto.TokenResponse;
import com.tbag.tbag_backend.domain.Auth.jwt.TokenProvider;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${jwt.refresh_expired-time}")
    long refreshExpired;

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            redisTemplate.opsForValue().getAndDelete(authentication.getName());
            clearSessionAndCookies(request, response);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        else {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found");
        }

    }

    private void clearSessionAndCookies(HttpServletRequest request, HttpServletResponse response) {

        request.getSession().invalidate();
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    public TokenResponse refreshToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TokenResponse jwt = tokenProvider.createRefreshToken(authentication);
        redisTemplate.opsForValue().set(
                authentication.getName(),
                jwt.getRefreshToken(),
                refreshExpired,
                TimeUnit.SECONDS
        );
        return jwt;
    }

}
