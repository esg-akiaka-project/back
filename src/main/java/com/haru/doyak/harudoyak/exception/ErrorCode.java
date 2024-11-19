package com.haru.doyak.harudoyak.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ERROR_CODE(HttpStatus.BAD_REQUEST, "errorMessage"),
    // 400 Bad Request
    NULL_VALUE(HttpStatus.BAD_REQUEST, "필수값이 비었습니다."),

    // 401 Unauthorized
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호 입니다."),

    // 403 Forbidden
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰 입니다"), // 토큰이 잘못됐을시 새로 로그인 위한 상태코드 401로 설정
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다"),
    EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다"),

    // 404 Not Found
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "탈퇴한 회원이거나 없는 회원 입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "이미 삭제된 게시글이거나 없는 게시글 입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 없습니다."),


    // 405 Method Not Allowed


    // 409 Conflictct
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "이미 가입된 회원"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "존재하는 닉네임"),


    // 500 Internal Server Error
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "unknown error");

    private final HttpStatus status;
    private final String errorMessage;
}
