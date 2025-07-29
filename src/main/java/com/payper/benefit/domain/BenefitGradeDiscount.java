package com.payper.benefit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitGradeDiscount {
    private Integer benefitGradeDiscountId;
    private Integer gradeId;
    private Integer benefitId;
    private DiscountType type; // 할인 타입 enum(RATE, FIXED_AMOUNT)
    private Long amount; // 할인 금액
    private Long minPayment; // 혜택 적용 최소 금액 default 0 nullable
    private Long limitCount; // 최대 할인 횟수 nullable
    private Long limitAmount; // 최대 할인 금액 nullable
}