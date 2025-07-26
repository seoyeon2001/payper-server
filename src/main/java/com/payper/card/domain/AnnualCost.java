package com.payper.card.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnualCost {
    private Integer annualCostId;
    private Integer cardId;
    private String brandName;
    private Long annualFee;
    private Long coAnnualFee;
}