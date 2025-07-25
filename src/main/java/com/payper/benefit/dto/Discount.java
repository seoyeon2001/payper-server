package com.payper.benefit.dto;

import lombok.Data;

@Data
public class Discount{
	private String type;
	private int amount;
	private int limitCount;
	private int limitAmount;
}