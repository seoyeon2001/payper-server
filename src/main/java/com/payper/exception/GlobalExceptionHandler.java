package com.payper.exception;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 사용 방법
     * throw new TemplateException(HttpStatus.CONFLICT,"이미 connected id가 존재합니다.");
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        return ErrorResponse.build(e.getHttpStatus(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception e, HttpServletRequest request) {
        return ErrorResponse.build(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.", request.getRequestURI());
    }
}

