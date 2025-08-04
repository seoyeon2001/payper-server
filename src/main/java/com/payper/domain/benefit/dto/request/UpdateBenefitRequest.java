package com.payper.domain.benefit.dto.request;

import lombok.Data;

@Data
public class UpdateBenefitRequest {
    private String title;
    private String summary;
    private String description;
    private String iconUrl;
}
