package com.payper.external.codef.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ConnectedIdUpdateFailedException extends CustomException {
    public ConnectedIdUpdateFailedException(Integer userId) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format("userId: %d - 해당 유저의 connected id 갱신에 실패했습니다.", userId));
    }
}
