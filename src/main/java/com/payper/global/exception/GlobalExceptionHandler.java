package com.payper.global.exception;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * 사용 방법
     * throw new TemplateException();
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        return ErrorResponse.build(e.getHttpStatus(), e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerError(Exception e, HttpServletRequest request) {
        log.error("===== 서버 내부 오류 발생 =====");
        log.error("Exception Type: {}", e.getClass().getSimpleName());
        log.error("Exception Message: {}", e.getMessage());
        log.error("Full Stack Trace: ", e);
        return ErrorResponse.build(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.", request.getRequestURI());
    }
}

