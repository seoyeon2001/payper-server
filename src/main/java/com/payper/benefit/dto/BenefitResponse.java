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
	private Integer id;
	private String title;
	private String summary;
	private String description;
	private String iconUrl;
	private LimitResponse limit;
	private List<BenefitGradeResponse> benefitGradeList;
	private List<CategoryResponse> categoryList;
	private List<PartnerResponse> partnerList;
	private Long minPayment;
}