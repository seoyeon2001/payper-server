package com.payper.domain.card.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CardExistedNotFoundException extends CustomException {
    public CardExistedNotFoundException() {
        super(HttpStatus.NOT_FOUND, "등록되지 않은 카드입니다.");
    }
}
