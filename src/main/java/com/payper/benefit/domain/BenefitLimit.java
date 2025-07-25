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
    private Long benefitLimitId;

    private Long benefitId;

    private Integer limitCountPerDay;

    private Integer limitCountPerMonth;

    private Integer limitCountPerYear;

    private Integer limitAmountPerPay;
}