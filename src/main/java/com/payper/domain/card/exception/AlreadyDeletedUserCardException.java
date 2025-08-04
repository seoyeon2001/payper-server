package com.payper.domain.card.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;
public class AlreadyDeletedUserCardException extends CustomException {
    public AlreadyDeletedUserCardException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "이미 삭제된 카드입니다.");
    }
}