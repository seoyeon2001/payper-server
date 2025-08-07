package com.payper.domain.category.exception;

import com.payper.global.exception.CustomException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends CustomException {
    public CategoryNotFoundException(Integer categoryId) {
        super(HttpStatus.NOT_FOUND, "해당하는 ID의 카테고리를 찾지 못했습니다. categoryId: " + categoryId);
    }
}
