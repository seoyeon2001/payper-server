package com.payper.domain.benefit.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class GradeDeletionFailedException extends CustomException {
    public GradeDeletionFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format("card-grade 삭제를 실패했습니다."));
    }
}
