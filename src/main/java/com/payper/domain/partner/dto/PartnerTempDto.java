package com.payper.domain.partner.dto;

import com.payper.domain.card.dto.CardResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerTempDto {
    private Integer partnerId;
    private String partnerName;
    private String partnerImageUrl;
    private String categoryName;
}
