package com.payper.domain.card.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class CardCompanyNotFoundException extends CustomException {
    public CardCompanyNotFoundException() {
        super(HttpStatus.NOT_FOUND, "해당 카드사가 존재하지 않습니다.");
    }
}
