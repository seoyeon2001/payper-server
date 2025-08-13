package com.payper.domain.card;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class SearchOptions {
    private String cardName;
    private String cardType;
    private List<String> categoryNames;
    private List<String> cardCompanyNames;
    private Integer userId;

    public static SearchOptions create(
            String cardName,String cardType,List<String> categoryNames,List<String> cardCompanyNames,Integer userId
    ){
        return builder()
                .cardName(cardName)
                .cardType(cardType)
                .categoryNames(categoryNames)
                .cardCompanyNames(cardCompanyNames)
                .userId(userId)
                .build();
    }
}
