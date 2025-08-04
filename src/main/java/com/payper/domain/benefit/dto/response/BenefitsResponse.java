package com.payper.domain.benefit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BenefitsResponse {
    private List<BenefitResponse> benefits;
}
