package com.payper.domain.category;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    Integer findIdByCategoryName(String categoryName);
}
