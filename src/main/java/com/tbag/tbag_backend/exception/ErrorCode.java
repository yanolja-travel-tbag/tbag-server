package com.tbag.tbag_backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {


    //400 Bad Request: 클라이언트의 요청이 잘못되어 서버가 요청을 이해할 수 없음
    MISMATCH_INFO(BAD_REQUEST, "There is no data related to request"),
    BLANK_REQUEST_VALUE(BAD_REQUEST, "Request value cannot be blank"),
    INVALID_LOST_FILTERING_SEARCH_CRITERIA(BAD_REQUEST, "Invalid filtering search criteria"),
    OAUTH_BAD_REQUEST(BAD_REQUEST, "Invalid request in oauth login"),


    //401 Unauthorized: 인증되지 않은 사용자가 보호된 페이지에 접근하려고 할 때 사용 -> token 관련 에러 처리만 담당
    HEADER_TOKEN_NOT_FOUND(UNAUTHORIZED, "token is not found in Bearer authorization header"),
    INVALID_TOKEN(UNAUTHORIZED, "token information is invalid"),
    TOKEN_EXPIRED(UNAUTHORIZED, "token is expired"),
    OAUTH_INVALID_AUTHORIZATION(UNAUTHORIZED, "oauth information is invalid"),


    //403 Forbidden: 인증된 사용자가 요청한 페이지에 접근할 권한이 없는 경우 사용
    INVALID_AUTHORITY(FORBIDDEN, "user is not authorized"),
    LIMIT_REQUEST(FORBIDDEN, "the request is limited"),


    //404 Not Found: 요청한 페이지가 서버에서 찾을 수 없는 경우 사용
    ENTITY_NOT_FOUND(NOT_FOUND, "Entity is not found"),


    //409 CONFLICT
    DUPLICATED_MEMBER(CONFLICT, "user is already existed"),
    DUPLICATED_DATA(CONFLICT, "Data already exists"),


    //500
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "Unexpected server error");


    private final HttpStatus httpStatus;
    private final String detail;
}
