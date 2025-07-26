package com.payper.benefit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitLimit {
    private Integer benefitLimitId;
    private Integer benefitId;
    private Long limitCountPerDay;
    private Long limitCountPerMonth;
    private Long limitCountPerYear;
    private Long limitAmountPerPay;
}