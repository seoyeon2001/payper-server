package com.payper.domain.category.dto;

import com.payper.domain.category.domain.Category;
import com.payper.global.exception.CustomIllegalArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
	private Integer id;
	private String name;
	private String imageUrl;

	public static CategoryResponse toDTO(Category category) {
		if(category == null) {
			throw new CustomIllegalArgumentException("category");
		}

		return CategoryResponse.builder()
				.id(category.getCategoryId())
				.name(category.getCategoryName())
				.imageUrl(category.getCategoryImageUrl())
				.build();
	}
}