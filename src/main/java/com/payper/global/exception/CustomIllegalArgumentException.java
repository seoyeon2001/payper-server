package com.payper.global.exception;

import org.springframework.http.HttpStatus;

public class CustomIllegalArgumentException extends CustomException {
    public CustomIllegalArgumentException(
            String msg
    ) {super(HttpStatus.INTERNAL_SERVER_ERROR, msg+" argument is null!");}
}
