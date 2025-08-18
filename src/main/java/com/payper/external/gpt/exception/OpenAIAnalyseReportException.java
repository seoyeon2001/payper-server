package com.payper.external.gpt.exception;

import com.payper.global.exception.CustomException;
import org.springframework.http.HttpStatus;

public class OpenAIAnalyseReportException extends CustomException {
    public OpenAIAnalyseReportException() {
        super(HttpStatus.BAD_GATEWAY, "OpenAI의 리포트 분석 중 오류가 발생했습니다.");
    }
}

