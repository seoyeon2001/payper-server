package com.payper.benefit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Benefit {
    private Long benefitId;

    private Long cardId;

    private String benefitTitle;

    private String benefitSummary;

    private String benefitIconUrl;

    private Integer minPayment;

    private String benefitDescription;
}