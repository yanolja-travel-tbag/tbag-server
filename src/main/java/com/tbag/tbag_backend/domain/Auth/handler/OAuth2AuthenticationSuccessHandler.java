package com.tbag.tbag_backend.domain.Auth.handler;

import com.tbag.tbag_backend.domain.Auth.dto.TokenResponse;
import com.tbag.tbag_backend.domain.Auth.jwt.TokenProvider;
import com.tbag.tbag_backend.domain.User.entity.User;
import com.tbag.tbag_backend.domain.User.repository.UserRepository;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed.");
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }


    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        TokenResponse jwt = tokenProvider.createToken(authentication);

        User user = userRepository.findBySocialIdAndIsActivatedIsTrue(Long.valueOf(authentication.getName()))
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found"));

        return UriComponentsBuilder.fromUriString("/oauth2/redirect")
                .queryParam("userId", user.getId())
                .queryParam("isRegistered", user.getIsRegistered())
                .queryParam("accessToken", jwt.getAccessToken())
                .queryParam("refreshToken", jwt.getRefreshToken())
                .build().toUriString();
    }

}