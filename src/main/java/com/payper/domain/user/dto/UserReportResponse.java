package com.payper.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReportResponse {
    private String startDate;
    private String endDate;
    private String reportAlias;
    private List<ReportSummary> reportSummaries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportSummary {
        private String category;
        private Long amount;
    }

    public static UserReportResponse ofDto(String startDate,
                                           String endDate,
                                           String reportAlias,
                                           List<ReportSummary> reportSummaries) {
        return UserReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .reportAlias(reportAlias)
                .reportSummaries(reportSummaries).build();
    }
}
