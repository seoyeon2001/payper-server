package com.payper.external.crawling.dto;
import lombok.Data;

import java.util.List;

@Data
public class CardData {
    public String cardName;
    public String companyName;
    public String cardType;
    public String imageUrl;
    public String issueUrl;
    public List<Benefit> benefits;
    public String gradeDescription;
    public String annualFee;
}
