package com.payper.benefit.dto;

import java.util.List;

import com.payper.category.dto.CategoryResponse;
import com.payper.category.dto.PartnerResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitResponse {
	private int id;
	private String title;
	private String summary;
	private String description;
	private String iconUrl;
	private LimitResponse limit;
	private List<BenefitGradeResponse> benefitGrades;
	private List<CategoryResponse> categories;
	private List<PartnerResponse> partners;
	private int minPayment;
}