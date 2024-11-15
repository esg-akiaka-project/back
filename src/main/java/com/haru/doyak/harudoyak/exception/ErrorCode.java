package com.haru.doyak.harudoyak.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ERROR_CODE(HttpStatus.BAD_REQUEST, "errorMessage"),
    // 400 Bad Request


    // 403 Forbidden
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰 입니다"), // 토큰이 잘못됐을시 새로 로그인 위한 상태코드 401로 설정
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다"),
    EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다"),

    // 404 Not Found


    // 405 Method Not Allowed


    // 409 Conflictct
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "이미 가입된 회원"),

    // 500 Internal Server Error
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "unknown error");

    private final HttpStatus status;
    private final String errorMessage;
}
