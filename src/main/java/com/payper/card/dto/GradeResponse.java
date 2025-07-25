package com.payper.card.dto;

import lombok.Data;

@Data
public class GradeResponse {
	private int id;
	private int start;
	private int end;
	private int totalDiscount;
}