package com.payper.exception;

import org.springframework.http.HttpStatus;

public class TemplateException extends CustomException {
    public TemplateException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
