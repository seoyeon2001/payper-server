package com.payper.category.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partner {
    private Long partnerId;

    private Long categoryId;

    private Long benefitId;

    private String partnerName;
}