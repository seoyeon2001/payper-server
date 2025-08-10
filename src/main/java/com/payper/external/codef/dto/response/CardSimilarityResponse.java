package com.payper.external.codef.dto.response;

/**
 * 내 카드를 기준으로 DB에 저장된 카드의 이름과 비교해서
 * DB에 저장된 cardName의 유사도를 반환
 */

public record CardSimilarityResponse(
        String cardName,
        double similarity
//        MatchType matchType
) {}