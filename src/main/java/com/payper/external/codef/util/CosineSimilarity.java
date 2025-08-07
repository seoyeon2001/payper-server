//package com.payper.external.codef.util;
//
//import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class CosineSimilarity {
//    // 문자열 두 개의 코사인 유사도를 계산
//    public static double compute(String str1, String str2) {
//        Map<String, Integer> freq1 = getTermFrequency(str1);
//        Map<String, Integer> freq2 = getTermFrequency(str2);
//
//        Set<String> allTokens = new HashSet<>();
//        allTokens.addAll(freq1.keySet());
//        allTokens.addAll(freq2.keySet());
//
//        int[] vec1 = new int[allTokens.size()];
//        int[] vec2 = new int[allTokens.size()];
//
//        int i = 0;
//        for (String token : allTokens) {
//            vec1[i] = freq1.getOrDefault(token, 0);
//            vec2[i] = freq2.getOrDefault(token, 0);
//            i++;
//        }
//
//        return cosineSimilarity(vec1, vec2);
//    }
//
//    // 문자열을 공백 단위로 쪼개고 단어 빈도 계산
//    private static Map<String, Integer> getTermFrequency(String text) {
//        Map<String, Integer> freqMap = new HashMap<>();
//        for (String word : text.split("\\s+")) {
//            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
//        }
//        return freqMap;
//    }
//
//    // 벡터 간 코사인 유사도 계산
//    private static double cosineSimilarity(int[] vec1, int[] vec2) {
//        int dot = 0;
//        double normA = 0.0;
//        double normB = 0.0;
//
//        for (int i = 0; i < vec1.length; i++) {
//            dot += vec1[i] * vec2[i];
//            normA += Math.pow(vec1[i], 2);
//            normB += Math.pow(vec2[i], 2);
//        }
//
//        return (normA == 0 || normB == 0) ? 0.0 : (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
//    }
//
//    // 전처리
//    public static String preprocessCardName(String name) {
//        // 제거할 단어 리스트
//        /**
//         * 카드: 의미있는 단어가 아님 - 공통으로 들어감
//         * 신용 / 체크: type에서 신용, 체크를 먼저 확인한다면 의미있는 값이 아님
//         */
//        List<String> removeWords = Arrays.asList("카드", "신용", "체크");
//
//        // 공백 기준 토큰 분리 후, 제거 단어 필터링
//        StringBuilder result = new StringBuilder();
//        for (String word : name.split("\\s+")) {
//            if (!removeWords.contains(word)) {
//                result.append(word).append(" ");
//            }
//        }
//
//        return result.toString().trim();
//    }
//
//
//    // 예제 실행
//    public static void main(String[] args) {
//        String apiCardName = "삼성 4 V4";
//        String cleanApiCardName = preprocessCardName(apiCardName);
//        System.out.println("내가 가져온 카드 이름 = " + cleanApiCardName);
//
//        List<String> dbCardNames = Arrays.asList(
//                "삼성카드 4 V4",
//                "삼성카드 taptap",
//                "국민 청춘 체크카드",
//                "하나카드 UNIVERSE 2.0"
//        );
//
//        for (String dbName : dbCardNames) {
//            String cleanDbName = preprocessCardName(dbName);
//            System.out.println("DB에서 1차 추출한 후보들 = " + cleanDbName);
//            double score = compute(cleanApiCardName, cleanDbName);
//            System.out.printf("유사도(%.2f): %s\n", score, cleanDbName);
//            System.out.println("====================================");
//        }
//    }
//}
//
