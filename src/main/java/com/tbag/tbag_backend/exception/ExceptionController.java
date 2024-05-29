package com.tbag.tbag_backend.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception")
@RequiredArgsConstructor
public class ExceptionController {

    @GetMapping("/")
    public String error1() throws Exception {
        throw new Exception("test");
    }

    @GetMapping("/runtime")
    public String error2() throws Exception {
        throw new RuntimeException("test");
    }

    @GetMapping("/custom500")
    public String error3() throws Exception {
        throw new CustomException(ErrorCode.SERVER_ERROR, "500 server error!");
    }

    @GetMapping("/custom401")
    public String error4() throws Exception {
        throw new CustomException(ErrorCode.INVALID_TOKEN, "Invalid Token!");
    }

}
