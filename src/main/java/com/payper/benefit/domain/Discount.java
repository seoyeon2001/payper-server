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
    private Long discountId;

    private String type;

    private Integer amount;

    private Integer limitCount;

    private Integer limitAmount;
}