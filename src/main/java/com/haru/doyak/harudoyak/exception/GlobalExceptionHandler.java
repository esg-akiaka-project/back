package com.haru.doyak.harudoyak.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }
}
