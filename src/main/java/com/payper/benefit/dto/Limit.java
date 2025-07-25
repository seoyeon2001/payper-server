package com.payper.benefit.dto;

import lombok.Data;

@Data
public class Limit{
	private int limitCountPerDay;
	private int limitCountPerMonth;
	private int limitCountPerYear;
	private int limitAmountPerPay;
}