package com.payper.domain.card.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CardRegisterationFailedException extends CustomException {
    public CardRegisterationFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "해당 카드가 등록되지 않았습니다.");
    }
}
