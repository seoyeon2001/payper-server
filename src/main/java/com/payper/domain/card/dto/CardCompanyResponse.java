package com.payper.domain.card.dto;

import com.payper.domain.card.domain.CardCompany;
import com.payper.global.exception.CustomIllegalArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCompanyResponse {
    private Integer id;
    private String name;

    public static CardCompanyResponse toDTO(CardCompany cardCompany) {
        if(cardCompany == null) {
            throw new CustomIllegalArgumentException("cardCompany");
        }

        return CardCompanyResponse.builder()
                .id(cardCompany.getCompanyId())
                .name(cardCompany.getCompanyName())
                .build();
    }
}
