package com.payper.benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LimitResponse {
	private int limitCountPerDay;
	private int limitCountPerMonth;
	private int limitCountPerYear;
	private int limitAmountPerPay;
}