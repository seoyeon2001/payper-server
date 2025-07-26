package com.payper.category.domain;

import java.util.Date;
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
    private Integer benefitId;
    private String categoryName;
    private Integer preCategoryId;
    private List<Partner> partnerList;
}