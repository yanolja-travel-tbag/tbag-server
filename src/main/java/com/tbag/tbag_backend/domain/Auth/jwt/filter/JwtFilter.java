package com.tbag.tbag_backend.domain.Auth.jwt.filter;

import com.tbag.tbag_backend.domain.Auth.jwt.TokenProvider;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) throws ServletException, IOException {

        // 토큰의 인증 정보를 Security Context에 저장하는 역할 수행

        try {
            String jwt = tokenProvider.resolveToken(request);

            tokenProvider.validate(jwt);
            if (jwt != null) {
                Authentication authentication = tokenProvider.resolveFrom(jwt, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                throw new CustomException(ErrorCode.INVALID_TOKEN, "token is invalid in jwt filter");
            }

        } catch (CustomException e) {
//            log.info("trace id " + traceId + " has a jwt filter error, " + e.getValue());
        } catch (Exception e) {
//            log.info("trace id " + traceId + " has a jwt filter error, " + e.getMessage());
        }

        chain.doFilter(request, response);

    }


}
