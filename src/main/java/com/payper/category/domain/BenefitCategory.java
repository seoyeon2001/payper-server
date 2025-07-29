package com.payper.category.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitCategory {
    private Integer benefitCategoryId;
    private Integer benefitId;
    private Integer categoryId;
}