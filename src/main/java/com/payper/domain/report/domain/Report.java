package com.payper.domain.report.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Report {
    private Integer reportId;
    private Integer userId;
    private LocalDate month;
    private String payload;
    private Date createdAt;
}
