package com.payper.domain.user.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class UserCardDeletionFailedException extends CustomException {
    public UserCardDeletionFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "유저-카드 삭제를 실패했습니다.");
    }
}
