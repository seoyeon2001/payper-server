package com.payper.category.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private Integer categoryId;
    private String categoryName;
    private Integer preCategoryId; // 상위 카테고리 Id nullable
    private List<BenefitCategory> benefitCategoryList;
}