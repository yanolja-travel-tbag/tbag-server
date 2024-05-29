package com.tbag.tbag_backend.domain.Auth.controller;

import com.tbag.tbag_backend.exception.CustomException;
import com.tbag.tbag_backend.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuthController {

    @GetMapping("/redirect")
    public String handleOAuth2Redirect(@RequestParam(required = false) String error) {
        if (error != null && !error.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_AUTHORITY, error);
        }
        return "";
    }

}

