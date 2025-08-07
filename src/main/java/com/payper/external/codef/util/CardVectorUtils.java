package com.payper.external.codef.util;

import java.util.*;

public class CardVectorUtils {

    // 벡터화: 모든 토큰 집합을 기준으로 벡터를 구성
    public static int[] toVector(List<String> tokens, List<String> allTokens) {
        int[] vector = new int[allTokens.size()];
        for (int i = 0; i < allTokens.size(); i++) {
            String word = allTokens.get(i);
            long count = tokens.stream().filter(t -> t.equals(word)).count();
            vector[i] = (int) count;
        }
        return vector;
    }

    // 코사인 유사도 계산
    public static double cosineSimilarity(int[] vec1, int[] vec2) {
        int dot = 0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            normA += Math.pow(vec1[i], 2);
            normB += Math.pow(vec2[i], 2);
        }

        return (normA == 0 || normB == 0) ? 0.0 : dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

