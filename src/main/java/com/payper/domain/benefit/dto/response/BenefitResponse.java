package com.payper.domain.benefit.dto.response;

import java.util.Collections;
import java.util.List;

import com.payper.domain.benefit.domain.Benefit;
import com.payper.domain.category.dto.CategoryResponse;
import com.payper.global.exception.CustomIllegalArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitResponse {
	private Integer id;
	private String title;
	private String summary;
	private String description;
	private String iconUrl;

	private List<BenefitGradeDiscountResponse> benefitGrades;
	private List<CategoryResponse> categories;

	public static BenefitResponse toDTO(Benefit benefit,
		List<BenefitGradeDiscountResponse> benefitGrades,
		List<CategoryResponse> categories) {

		if(benefit == null) {
			throw new CustomIllegalArgumentException("benefit");
		}

		return BenefitResponse.builder()
				.id(benefit.getBenefitId())
				.title(benefit.getBenefitTitle())
				.summary(benefit.getBenefitSummary())
				.description(benefit.getBenefitDescription())
				.iconUrl(benefit.getBenefitIconUrl())
				.benefitGrades(benefitGrades != null ? benefitGrades:Collections.emptyList())
				.categories(categories != null ? categories: Collections.emptyList())
				.build();
	}
}