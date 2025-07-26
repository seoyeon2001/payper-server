package com.payper.benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountResponse {
	private String type;
	private int amount;
	private int limitCount;
	private int limitAmount;
}