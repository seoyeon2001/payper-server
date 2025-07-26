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
    private Integer partnerId;
    private Integer categoryId;
    private Integer benefitId;
    private String partnerName;
}