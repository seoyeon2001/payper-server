package com.payper.external.codef.util;

import com.payper.external.codef.dto.response.CardSimilarityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardSimilarityService {

    private static final double AUTO_MATCH_THRESHOLD = 0.7;
    private static final double CANDIDATE_MATCH_THRESHOLD = 0.5;

    // Top-N 추천
    public List<CardSimilarityResponse> recommendSimilarCards(String apiCardName, int topN, List<String> dbCardNames) {

        List<String> apiTokens = KoreanTokenizer.tokenizeAndClean(apiCardName);
        if (apiTokens.isEmpty() || dbCardNames == null || dbCardNames.isEmpty()) {
            return Collections.emptyList();
        }

        // DB 토큰 맵
        Set<String> tokenSet = new HashSet<>(apiTokens);
        Map<String, List<String>> dbTokenMap = new HashMap<>();

        for (String dbName : dbCardNames) {
            List<String> tokens = KoreanTokenizer.tokenizeAndClean(dbName);
            dbTokenMap.put(dbName, tokens);
            tokenSet.addAll(tokens);
        }

        // 어휘 사전(인덱스) + IDF (DB를 말뭉치로 사용)
        List<String> allTokens = new ArrayList<>(tokenSet);
        Map<String, Integer> index = CardVectorUtils.buildIndex(allTokens);
        Map<String, Double> idf = CardVectorUtils.computeIdf(dbTokenMap.values(), index);

        // 벡터화: TF‑IDF 사용 (정확도↑)
        double[] apiVector = CardVectorUtils.tfidfVector(apiTokens, index, idf);

        // 유사도 계산 → 정렬 → 상위 N
        List<CardSimilarityResponse> allResults =  dbTokenMap.entrySet().stream()
                .map(e -> {
                    double[] dbVec = CardVectorUtils.tfidfVector(e.getValue(), index, idf);
                    double sim = CardVectorUtils.cosineSimilarity(apiVector, dbVec);
                    return new CardSimilarityResponse(e.getKey(), sim);
                })
                // 유사도 내림차순 정렬 후 Top-N 반환
                .sorted(Comparator.comparingDouble(CardSimilarityResponse::similarity).reversed())
                .limit(topN)
                .toList();


//        if (topN == 1) {
            if (allResults.isEmpty()) {
                return Collections.emptyList(); // 유사도 계산 대상 자체가 없음
            }

            CardSimilarityResponse topResult = allResults.get(0);
            double similarity = topResult.similarity();

            if (similarity >= AUTO_MATCH_THRESHOLD) {
                // 1. 유사도 >= 0.7 → 자동 매칭
                log.debug("자동 매칭 대상: {} (유사도: {})", topResult.cardName(), similarity);
                return List.of(topResult);
            } else if (similarity >= CANDIDATE_MATCH_THRESHOLD) {
                // 2. 0.5 <= 유사도 < 0.7 → 후보 매칭
                log.debug("후보 매칭 대상: {} (유사도: {})", topResult.cardName(), similarity);
                return List.of(topResult);
            } else {
                // 3. 유사도 < 0.5 → 매칭 없음
                log.debug("매칭 불가: {} (유사도: {})", topResult.cardName(), similarity);
                return Collections.emptyList();
            }
//        }

    }
}

