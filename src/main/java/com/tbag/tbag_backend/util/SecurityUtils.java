package com.tbag.tbag_backend.util;

import com.tbag.tbag_backend.domain.User.entity.User;
import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication authentication = getAuthentication();
        return extractUserFromPrincipal(authentication.getPrincipal());
    }

    public static Integer getCurrentUserId() {
        Authentication authentication = getAuthentication();
        User user = extractUserFromPrincipal(authentication.getPrincipal());
        return user.getId();
    }

    private static User extractUserFromPrincipal(Object principal) {
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUser();
        }
        throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found");
    }

    private static boolean isAuthenticated(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated();
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND, "User not found");
        }
        return authentication;
    }
}
