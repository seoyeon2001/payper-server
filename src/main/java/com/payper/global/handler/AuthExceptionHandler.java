package com.payper.global.handler;

import com.payper.global.auth.AuthController;
import com.payper.global.auth.exception.InvalidRefreshTokenException;
import com.payper.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// TODO: GlobalExceptionHandler 대신 동작하도록 설정
@RestControllerAdvice(assignableTypes = {AuthController.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AuthExceptionHandler {

    @ExceptionHandler(value = InvalidRefreshTokenException.class)
    public String handleInvalidRefreshTokenException(InvalidRefreshTokenException e, HttpServletRequest request) {
        log.error("Invalid refresh token", e);
        return ErrorResponse.build(HttpStatus.UNAUTHORIZED, "Token reissue failed", request.getRequestURI()).toString();
    }
}
