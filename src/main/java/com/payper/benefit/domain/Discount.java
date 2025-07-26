package com.payper.benefit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {
    private Integer discountId;
    private String type;
    private Long amount;
    private Long limitCount;
    private Long limitAmount;
    private Integer benefitGradeId;
}