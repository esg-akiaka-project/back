package com.haru.doyak.harudoyak.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.name();
        this.errorMessage = errorCode.getErrorMessage();
    }

    public CustomException(HttpStatus status, String errorCode, String errorMessage) {
        super(errorMessage);
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
