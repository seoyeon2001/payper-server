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
public class Benefit {
    public String title;
    public String summary;
    public String description;
    public List<String> categories;
    public Discount discount;
}