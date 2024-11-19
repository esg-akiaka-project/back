package com.haru.doyak.harudoyak.exception;

import com.haru.doyak.harudoyak.domain.auth.EmailService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ERROR_CODE(HttpStatus.BAD_REQUEST, "errorMessage"),
    // 400 Bad Request
    NULL_VALUE(HttpStatus.BAD_REQUEST, "필수값이 비었습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증 후 시도해주새요."),

    // 401 Unauthorized
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호 입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "사용 불가능한 토큰 입니다"), // 토큰이 잘못됐을시 새로 로그인 위한 상태코드 401로 설정
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다"),
    EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다"),

    // 403 Forbidden
    DIFFERENT_MEMBER_TOKEN(HttpStatus.FORBIDDEN, "다른 사용자의 토큰"),


    // 404 Not Found
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "탈퇴한 회원이거나 없는 회원 입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "이미 삭제된 게시글이거나 없는 게시글 입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 없습니다."),

    // 405 Method Not Allowed


    // 409 Conflictct
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "이미 가입된 회원"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "존재하는 닉네임"),


    // 500 Internal Server Error
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "unknown error"),
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 실패 : 다시 시도하세요");

    private final HttpStatus status;
    private final String errorMessage;
}
