package com.payper.card.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnualCost {
    private Long annualCostId;

    private Long cardId;

    private String brandName;

    private Integer annualFee;

    private Integer coAnnualFee;
}