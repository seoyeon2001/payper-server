package com.payper.exception;

import org.springframework.http.HttpStatus;

public class TemplateException extends CustomException {
    public TemplateException() {
        super(HttpStatus.NOT_FOUND, "예시 TemplateException 입니다.");
    }
}
