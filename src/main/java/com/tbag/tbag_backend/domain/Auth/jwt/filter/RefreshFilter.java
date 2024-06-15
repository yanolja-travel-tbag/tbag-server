package com.tbag.tbag_backend.domain.Auth.jwt.filter;

import com.tbag.tbag_backend.domain.Auth.jwt.TokenProvider;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RefreshFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) throws ServletException, IOException {


        if (request.getRequestURI().equals("/auth/tokenRefresh")) {

            String refresh_token = tokenProvider.resolveToken(request);

            try {
                if (!tokenProvider.validate(refresh_token)) {
                    throw new CustomException(ErrorCode.INVALID_TOKEN, "Invalid refresh token supplied");
                }
                Authentication authentication = tokenProvider.resolveFrom(refresh_token, request);
                String refreshToken = redisTemplate.opsForValue().get(authentication.getName());

                if (refreshToken == null) {
                    throw new CustomException(ErrorCode.INVALID_TOKEN, "Refresh Token not found.");
                } else if (!refreshToken.equals(refresh_token)) {
                    throw new CustomException(ErrorCode.INVALID_TOKEN, "Refresh Token doesn't match.");
                }

            } catch (AuthenticationException e) {
                throw new CustomException(ErrorCode.INVALID_TOKEN, "Refresh Token doesn't match.");
            }
        }

        chain.doFilter(request, response);

    }

}
