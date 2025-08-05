package com.payper.domain.category;

import com.payper.domain.category.dto.RegisterCategoryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CategoryMapper {
    Integer findIdByCategoryName(String categoryName);
    String findNameByPartnerId(Integer partnerId);
    void registerCategory(@Param("request") RegisterCategoryRequest request);
    void registerBenefitCategory(@Param("benefitId") Integer benefitId,
                                 @Param("categoryId") Integer categoryId);
}
