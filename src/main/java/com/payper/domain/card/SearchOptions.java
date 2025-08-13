package com.payper.domain.card;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class SearchOptions {
    private String keyword;
    private String cardType;
    private List<String> categoryNames;
    private List<String> cardCompanyNames;

    public static SearchOptions create(
            String keyword, String cardType, List<String> categoryNames, List<String> cardCompanyNames
    ){
        return builder()
                .keyword(keyword)
                .cardType(cardType)
                .categoryNames(categoryNames)
                .cardCompanyNames(cardCompanyNames)
                .build();
    }
}
