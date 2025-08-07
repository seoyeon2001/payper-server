package com.payper.external.codef.util;

import com.payper.external.codef.dto.response.CardSimilarityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CardSimilarityService {

    // Top-N 추천
    public List<CardSimilarityResponse> recommendSimilarCards(String apiCardName, int topN, List<String> dbCardNames) {

        List<String> apiTokens = KoreanTokenizer.tokenizeAndClean(apiCardName);

        // 모든 형태소 토큰 수집 (전체 벡터 공간 구성)
        Set<String> tokenSet = new HashSet<>(apiTokens);
        Map<String, List<String>> dbTokenMap = new HashMap<>();

        for (String dbName : dbCardNames) {
            List<String> tokens = KoreanTokenizer.tokenizeAndClean(dbName);
            dbTokenMap.put(dbName, tokens);
            tokenSet.addAll(tokens);
        }

        List<String> allTokens = new ArrayList<>(tokenSet); // 전체 차원 기준

        // API 카드 벡터 생성
        int[] apiVector = CardVectorUtils.toVector(apiTokens, allTokens);


        /***
         *         // DB 카드별 유사도 계산
         *         List<CardSimilarityDto> results = new ArrayList<>();
         *         for (Map.Entry<String, List<String>> entry : dbTokenMap.entrySet()) {
         *             int[] dbVector = CardVectorUtils.toVector(entry.getValue(), allTokens);
         *             double similarity = CardVectorUtils.cosineSimilarity(apiVector, dbVector);
         *             results.add(new CardSimilarityDto(entry.getKey(), similarity));
         *         }
         *
         *         // 유사도 내림차순 정렬 후 Top-N 반환
         *         return results.stream()
         *                 .sorted(Comparator.comparingDouble(CardSimilarityDto::similarity).reversed())
         *                 .limit(topN)
         *                 .collect(Collectors.toList());
         */
        // DB 카드별 유사도 계산
        return dbTokenMap.entrySet().stream()
                .map(e -> {
                    int[] dbVec = CardVectorUtils.toVector(e.getValue(), allTokens);
                    double sim = CardVectorUtils.cosineSimilarity(apiVector, dbVec);
                    return new CardSimilarityResponse(e.getKey(), sim);
                })
                // 유사도 내림차순 정렬 후 Top-N 반환
                .sorted(Comparator.comparingDouble(CardSimilarityResponse::similarity).reversed())
                .limit(topN)
                .toList();
    }
}

