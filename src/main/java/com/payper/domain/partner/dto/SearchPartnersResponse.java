package com.payper.domain.partner.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchPartnersResponse {
    private Integer id;
    private String name;
    private String partnerImageUrl;
    private Integer categoryId;
    private Integer preCategoryId;
    private String categoryName;
    private String categoryImageUrl;
}
