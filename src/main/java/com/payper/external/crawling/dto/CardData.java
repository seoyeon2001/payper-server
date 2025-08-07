package com.payper.external.crawling.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardData {
    public String cardName;
    public String companyName;
    public String cardType;
    public String imageUrl;
    public String issueUrl;
    public List<Benefit> benefits;
    public String annualFee;
    public Long prevMonthSpending;
}
