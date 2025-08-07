package com.payper.domain.category;

import com.payper.domain.category.domain.Category;
import com.payper.domain.category.dto.CategoryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {
    Integer findIdByCategoryName(String categoryName);
    void registerBenefitCategory(@Param("benefitId") Integer benefitId,
                                 @Param("categoryId") Integer categoryId);
    CategoryResponse findByPartnerId(Integer partnerId);
    List<Category> selectAll();

    Optional<Category> findById(Integer categoryId);
}
