package com.payper.domain.benefit.dto;

import lombok.Data;

@Data
public class CreateBenefitRequest {
    private Integer cardId;
    private String title;
    private String summary;
    private String description;
    private String iconUrl;
}
