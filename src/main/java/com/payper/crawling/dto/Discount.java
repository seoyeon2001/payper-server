package com.payper.crawling.dto;

import lombok.Data;

@Data
public class Discount {
    public String type;
    public Long amount;
    public Long minPayment;
    public Long limitCount;
    public Long limitAmount;
}
