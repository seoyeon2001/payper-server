package com.payper.domain.report.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class ReportDateUtil {
    private static final DateTimeFormatter F = DateTimeFormatter.BASIC_ISO_DATE; // YYYYMMDD

    @Getter
    @AllArgsConstructor
    public static class PeriodRangeDto {
        private LocalDate start;   // 해당 월의 1일
        private LocalDate end;     // 해당 월의 마지막 날
        private String startStr;   // YYYYMMDD 문자열
        private String endStr;     // YYYYMMDD 문자열
    }

    public static PeriodRangeDto resolvePeriod(String month) {
        LocalDate monthStart = YearMonth.parse(month).atDay(1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        return new PeriodRangeDto(
                monthStart,
                monthEnd,
                monthStart.format(F),
                monthEnd.format(F)
        );
    }
}
