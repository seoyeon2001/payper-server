package com.payper.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponse {
	private int id;
	private int start;
	private int end;
	private int totalDiscount;
}