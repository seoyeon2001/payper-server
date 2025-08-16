package com.payper.external.codef.util;

import java.util.*;

public class CardVectorUtils {
    // vocab 인덱스 생성
    public static Map<String, Integer> buildIndex(List<String> vocab) {
        Map<String, Integer> index = new HashMap<>(vocab.size() * 2);
        for (int i = 0; i < vocab.size(); i++) {
            index.put(vocab.get(i), i);
        }
        return index;
    }

    // 문서 모음(DB 카드들)을 코퍼스로 IDF 계산
    // idf = log( (N + 1) / (df + 1) ) + 1  〔smoothing〕
    public static Map<String, Double> computeIdf(Collection<List<String>> docs, Map<String, Integer> index) {
        int N = docs.size();
        double[] df = new double[index.size()];

        for (List<String> doc : docs) {
            // 한 문서 내 중복 제거된 토큰만 DF에 반영
            Set<String> uniq = new HashSet<>(doc);
            for (String t : uniq) {
                Integer idx = index.get(t);
                if (idx != null) df[idx] += 1.0;
            }
        }

        Map<String, Double> idf = new HashMap<>(index.size() * 2);
        for (Map.Entry<String, Integer> e : index.entrySet()) {
            double v = Math.log((N + 1.0) / (df[e.getValue()] + 1.0)) + 1.0;
            idf.put(e.getKey(), v);
        }
        return idf;
    }

    // TF‑IDF 벡터 (log-scaled TF 사용)
    public static double[] tfidfVector(List<String> tokens, Map<String, Integer> index, Map<String, Double> idf) {
        double[] vec = new double[index.size()];
        if (tokens == null || tokens.isEmpty()) return vec;

        // term freq
        Map<String, Integer> tf = new HashMap<>();
        for (String t : tokens) {
            tf.merge(t, 1, Integer::sum);
        }

        for (Map.Entry<String, Integer> e : tf.entrySet()) {
            Integer idx = index.get(e.getKey());
            if (idx == null) continue;
            double tfw = 1.0 + Math.log(e.getValue());    // log TF
            double idfw = idf.getOrDefault(e.getKey(), 1.0);
            vec[idx] = tfw * idfw;
        }
        return vec;
    }

    // 코사인 유사도
    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double dot = 0.0;
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

