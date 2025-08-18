package com.payper.domain.report.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class ReportPayloadSerializeException extends CustomException {
    public ReportPayloadSerializeException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "리포트를 JSON으로 변환하는 데 실패했습니다.");
    }
}