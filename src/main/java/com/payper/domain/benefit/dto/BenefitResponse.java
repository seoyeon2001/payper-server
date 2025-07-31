package com.payper.domain.benefit.dto;

import java.util.List;

import com.payper.domain.category.dto.CategoryResponse;
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
//	private LimitResponse limit;
	private List<BenefitGradeResponse> benefitGradeList;
	private List<CategoryResponse> categoryList;
	private Long minPayment;
}