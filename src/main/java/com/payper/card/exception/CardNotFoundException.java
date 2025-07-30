package com.payper.card.exception;

import com.payper.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CardNotFoundException extends CustomException {
    public CardNotFoundException() {
        super(HttpStatus.NOT_FOUND, "해당 카드가 존재하지 않습니다.");
    }
}
