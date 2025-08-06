package com.payper.external.codef.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class AlreadyLinkedCodefAccountException extends CustomException {
    public AlreadyLinkedCodefAccountException() {
        super(HttpStatus.CONFLICT, "이미 계정이 생성되어 connectedId가 있는 사용자입니다.");
    }
}
