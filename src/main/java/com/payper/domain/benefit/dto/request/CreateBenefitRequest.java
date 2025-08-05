package com.payper.domain.benefit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBenefitRequest {
    private Integer benefitId;
    private String title;
    private String summary;
    private String description;
    private String iconUrl;
}
