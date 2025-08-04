package com.payper.global.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@Slf4j
public abstract class CustomException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;

        // 공통 예외 로깅 처리
        log.error("[{}] {} - {}", this.getClass().getSimpleName(), httpStatus, message);
    }
}