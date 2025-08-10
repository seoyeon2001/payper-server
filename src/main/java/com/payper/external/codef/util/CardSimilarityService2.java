//package com.payper.external.codef.util;
//
//import com.payper.external.codef.dto.response.CardSimilarityResponse;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CardSimilarityService2 {
//    private static final double AUTO_MATCH_THRESHOLD = 0.7;
//    private static final double CANDIDATE_MATCH_THRESHOLD = 0.5;
//
//    // Top-N 추천
//    public Object recommendSimilarCards(String apiCardName, int topN, List<String> dbCardNames) {
//
//        // codef에서 가져온 카드 이름
//        List<String> apiTokens = KoreanTokenizer.tokenizeAndClean(apiCardName);
//
//        // 모든 형태소 토큰 수집 (전체 벡터 공간 구성)
//        Set<String> tokenSet = new HashSet<>(apiTokens);
//        Map<String, List<String>> dbTokenMap = new HashMap<>();
//
//        for (String dbName : dbCardNames) {
//            List<String> tokens = KoreanTokenizer.tokenizeAndClean(dbName);
//            dbTokenMap.put(dbName, tokens);
//            tokenSet.addAll(tokens);
//        }
//
//        List<String> allTokens = new ArrayList<>(tokenSet); // 전체 차원 기준
//
//        // API 카드 벡터 생성
////        int[] apiVector = CardVectorUtils.toVector(apiTokens, allTokens);
//
//        // DB 카드별 유사도 계산 + 매칭 타입 결정
//        int[] apiVector = CardVectorUtils.toVector(apiTokens, allTokens);
//        List<CardSimilarityResponse> allResults = dbTokenMap.entrySet().stream()
//                .map(e -> {
//                    int[] dbVec = CardVectorUtils.toVector(e.getValue(), allTokens);
//                    double sim = CardVectorUtils.cosineSimilarity(apiVector, dbVec);
//                    return new CardSimilarityResponse(e.getKey(), sim, determineMatchType(sim));
//                })
//                .limit(topN)
//                .sorted(Comparator.comparingDouble(CardSimilarityResponse::similarity).reversed())
//                .toList();
//
//        // 조건 분기
//        List<CardSimilarityResponse> autoMatches = allResults.stream()
//                .filter(r -> r.similarity() >= AUTO_MATCH_THRESHOLD)
//                .toList();
//
//        List<CardSimilarityResponse> candidates = allResults.stream()
//                .filter(r -> r.similarity() < AUTO_MATCH_THRESHOLD && r.similarity() >= CANDIDATE_MATCH_THRESHOLD)
//                .toList();
//
//        // 1. 유사도 >= 0.7 인게 여러 개 → 최고값 1개 반환
//        if (autoMatches.size() > 1) {
//            return autoMatches.stream()
//                    .max(Comparator.comparingDouble(CardSimilarityResponse::similarity))
//                    .orElse(null);
//        }
//
//        // 2. 유사도 >= 0.7 인게 1개 → 그대로 반환
//        else if (autoMatches.size() == 1) {
//            return autoMatches.get(0);
//        }
//
//        // 3. 유사도 0.5 ~ 0.7 인게 여러 개 → 프론트로 선택 요청
//        else if (candidates.size() > 1) {
//            return Map.of("action", "select", "candidates", candidates);
//        }
//
//        // 4. 유사도 0.5 ~ 0.7 인게 1개 → 프론트에서 확인 요청
//        else if (candidates.size() == 1) {
//            return Map.of("action", "confirm", "candidate", candidates.get(0));
//        }
//
//        // 5. 그 외 → 해당 카드 없음
//        return Map.of("action", "none", "message", "해당 카드를 찾을 수 없습니다.");
//
//
//
//        /***
//         *         // DB 카드별 유사도 계산
//         *         List<CardSimilarityDto> results = new ArrayList<>();
//         *         for (Map.Entry<String, List<String>> entry : dbTokenMap.entrySet()) {
//         *             int[] dbVector = CardVectorUtils.toVector(entry.getValue(), allTokens);
//         *             double similarity = CardVectorUtils.cosineSimilarity(apiVector, dbVector);
//         *             results.add(new CardSimilarityDto(entry.getKey(), similarity));
//         *         }
//         *
//         *         // 유사도 내림차순 정렬 후 Top-N 반환
//         *         return results.stream()
//         *                 .sorted(Comparator.comparingDouble(CardSimilarityDto::similarity).reversed())
//         *                 .limit(topN)
//         *                 .collect(Collectors.toList());
//         */
//
//    }
//
//    private MatchType determineMatchType(double similarity) {
//        if (similarity >= AUTO_MATCH_THRESHOLD) return MatchType.AUTO_MATCH;
//        if (similarity >= CANDIDATE_MATCH_THRESHOLD) return MatchType.CANDIDATE;
//        return MatchType.EXCLUDE;
//    }
//}
//
