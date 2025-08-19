package com.payper.domain.card.dto;

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
    private Integer page;
    private Integer limit;

    private static final Integer LIMIT=10;

    public static SearchOptions create(
            String keyword, String cardType,
            List<String> categoryNames,
            List<String> cardCompanyNames,
            Integer page
    ){
        return builder()
                .keyword(keyword)
                .cardType(cardType)
                .categoryNames(categoryNames)
                .cardCompanyNames(cardCompanyNames)
                .page(page*LIMIT)
                .limit(LIMIT)
                .build();
    }
}
