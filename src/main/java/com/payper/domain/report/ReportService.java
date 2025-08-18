package com.payper.domain.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.domain.report.exception.ReportPayloadParseException;
import com.payper.domain.report.exception.ReportPayloadSerializeException;
import com.payper.domain.report.util.ReportDateUtil;
import com.payper.domain.user.UserCardTransactionMapper;
import com.payper.domain.user.dto.UserReportResponse;
import com.payper.domain.user.dto.UserTransactionSummaryDto;
import com.payper.external.gpt.OpenAIExtractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserCardTransactionMapper userCardTransactionMapper;
    private final ReportMapper reportMapper;
    private final OpenAIExtractService openAIExtractService;

    private final ObjectMapper objectMapper;

    @Transactional
    public UserReportResponse analyzeMonth(Integer userId, String month) {
        ReportDateUtil.PeriodRangeDto period = ReportDateUtil.resolvePeriod(month);

        // 기존 리포트 확인
        String payload = reportMapper.findPayloadByUserAndMonth(userId, period.getStart());
        if (payload != null) return toResponse(payload);

        // 없으면 새로 분석
        List<UserTransactionSummaryDto> userTransactionSummaryDto
                = userCardTransactionMapper.getUserTransactionSummaryDto(userId, period.getStartStr(), period.getEndStr());
        UserReportResponse result = openAIExtractService.extract(userTransactionSummaryDto, period.getStartStr(), period.getEndStr());

        savePayload(userId, period.getStart(), result);
        return result;
    }

    protected UserReportResponse toResponse(String payload) {
        try {
            return objectMapper.readValue(payload, UserReportResponse.class);
        } catch (JsonProcessingException e) {
            throw new ReportPayloadParseException();
        }
    }

    private void savePayload(Integer userId, LocalDate start, UserReportResponse result) {
        try {
            String json = objectMapper.writeValueAsString(result);
            reportMapper.insert(userId, start, json);
        } catch (JsonProcessingException e) {
            throw new ReportPayloadSerializeException();
        }
    }
}
