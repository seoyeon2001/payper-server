package com.payper.domain.category;

import com.payper.domain.category.dto.CategoryResponse;
import com.payper.domain.category.dto.RegisterCategoryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CategoryMapper {
    Integer findIdByCategoryName(String categoryName);
    void registerCategory(@Param("request") RegisterCategoryRequest request);
    void registerBenefitCategory(@Param("benefitId") Integer benefitId,
                                 @Param("categoryId") Integer categoryId);
    CategoryResponse findByPartnerId(Integer partnerId);
}
