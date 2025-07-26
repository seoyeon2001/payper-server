package com.payper.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnualCostResponse {
	private String brandName;
	private String annualFee;
	private String coAnnualFee;
}