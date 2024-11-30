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
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증 후 시도해주새요."),
    BAD_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "일치하는 카테고리가 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못 입력된 데이터 입니다."),

    // 401 Unauthorized
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호 입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "사용 불가능한 토큰 입니다"), // 토큰이 잘못됐을시 새로 로그인 위한 상태코드 401로 설정
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰 입니다"),
    EMPTY_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 비어있습니다"),

    // 403 Forbidden
    DIFFERENT_MEMBER_TOKEN(HttpStatus.FORBIDDEN, "다른 사용자의 토큰"),


    // 404 Not Found
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "탈퇴한 회원이거나 없는 회원 입니다."),
    SHARE_DOYAK_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "서로도약 글 목록이 없습니다."),
    SHARE_DOYAK_NOT_FOUND(HttpStatus.NOT_FOUND, "이미 삭제된 서로도약 글이거나 없는 서로도약 글 입니다."),
    SHARE_DOYAK_NOT_AUTHOR(HttpStatus.NOT_FOUND, "서로도약 글의 작성자가 아닙니다."),
    DOYAK_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "도약하기 목록이 없습니다."),
    DOYAK_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 서로도약에 도약하기를 누르지 않았습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미 삭제된 파일이거나 없는 파일 입니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "이미 삭제된 댓글이거나 없는 댓글 입니다."),
    COMMENT_NOT_AUTHOR(HttpStatus.NOT_FOUND, "댓글의 작성자가 아닙니다."),
    COMMENT_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 목록이 없습니다."),
    LEVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 레벨이 없습니다."),
    LOG_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "도약기록 목록이 없습니다."),
    LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 도약기록이 없습니다."),
    LETTER_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "도약이 편지(피드백) 목록이 없습니다."),
    EMOTION_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "감정 목록이 없습니다."),
    TAG_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "태그 목록이 없습니다."),
    NOTIFICATION_NOT_FOND(HttpStatus.NOT_FOUND, "알림이 없습니다."),

    // 405 Method Not Allowed


    // 409 Conflictct
    DUPLICATE_MEMBER(HttpStatus.CONFLICT, "이미 가입된 회원"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "존재하는 닉네임"),


    // 500 Internal Server Error
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "unknown error"),
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 실패 : 다시 시도하세요"),
    SYNTAX_INVALID_FIELD(HttpStatus.INTERNAL_SERVER_ERROR, "필드 매핑이 잘못되었습니다."),
    EMPTY_VALUE(HttpStatus.INTERNAL_SERVER_ERROR, "원하는 값이 비어있습니다."),
    CONVERSION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "타입 변환이 실패했습니다."),
    COMMENT_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 테이블을 참조하는 테이블이 있습니다."),
    SYSTEM_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String errorMessage;
}
