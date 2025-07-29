package com.payper.crawling.dto;

import lombok.Data;
import java.util.List;

@Data
public class Benefit {
    public String title;
    public String summary;
    public String description;
    public List<String> categories;
    public Discount discount;
}