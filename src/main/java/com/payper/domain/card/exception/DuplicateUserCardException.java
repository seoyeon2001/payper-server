package com.payper.domain.card.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class DuplicateUserCardException extends CustomException {
    public DuplicateUserCardException(Integer userId, Integer cardId) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format("userId: %d, cardId: %d - 이미 등록된 카드입니다.", userId, cardId));
    }
}