//package com.payper.external.codef.util;
//
//import java.text.Normalizer;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//
//public class CardVectorUtils2 {
//
////    // 벡터화: 모든 토큰 집합을 기준으로 벡터를 구성
////    public static int[] toVector(List<String> tokens, List<String> allTokens) {
////        int[] vector = new int[allTokens.size()];
//////        for (int i = 0; i < allTokens.size(); i++) {
//////            String word = allTokens.get(i);
//////            long count = tokens.stream().filter(t -> t.equals(word)).count();
//////            vector[i] = (int) count;
//////        }
////
////        Map<String, Integer> tf = new HashMap<>();
////        for (String t : tokens) {
////            tf.merge(t, 1, Integer::sum);
////        }
////        for (int i = 0; i < allTokens.size(); i++) {
////            String word = allTokens.get(i);
////            vector[i] = tf.getOrDefault(word, 0);
////        }
////
////        return vector;
////    }
//
////    // 코사인 유사도 계산
////    public static double cosineSimilarity(int[] vec1, int[] vec2) {
////        int dot = 0;
////        double normA = 0.0;
////        double normB = 0.0;
////
////        for (int i = 0; i < vec1.length; i++) {
////            dot += vec1[i] * vec2[i];
////            normA += Math.pow(vec1[i], 2);
////            normB += Math.pow(vec2[i], 2);
////        }
////
////        return (normA == 0 || normB == 0) ? 0.0 : dot / (Math.sqrt(normA) * Math.sqrt(normB));
////    }
//
//    // vocab 인덱스 생성
//    public static Map<String, Integer> buildIndex(List<String> vocab) {
//        Map<String, Integer> index = new HashMap<>(vocab.size() * 2);
//        for (int i = 0; i < vocab.size(); i++) {
//            index.put(vocab.get(i), i);
//        }
//        return index;
//    }
//
//    // 문서 모음(DB 카드들)을 코퍼스로 IDF 계산
//    // idf = log( (N + 1) / (df + 1) ) + 1  〔smoothing〕
//    public static Map<String, Double> computeIdf(Collection<List<String>> docs,
//                                                 Map<String, Integer> index) {
//        int N = docs.size();
//        double[] df = new double[index.size()];
//
//        for (List<String> doc : docs) {
//            // 한 문서 내 중복 제거된 토큰만 DF에 반영
//            Set<String> uniq = new HashSet<>(doc);
//            for (String t : uniq) {
//                Integer idx = index.get(t);
//                if (idx != null) df[idx] += 1.0;
//            }
//        }
//
//        Map<String, Double> idf = new HashMap<>(index.size() * 2);
//        for (Map.Entry<String, Integer> e : index.entrySet()) {
//            double v = Math.log((N + 1.0) / (df[e.getValue()] + 1.0)) + 1.0;
//            idf.put(e.getKey(), v);
//        }
//        return idf;
//    }
//
//    // TF‑IDF 벡터 (log-scaled TF 사용)
//    public static double[] tfidfVector(List<String> tokens,
//                                       Map<String, Integer> index,
//                                       Map<String, Double> idf) {
//        double[] vec = new double[index.size()];
//        if (tokens == null || tokens.isEmpty()) return vec;
//
//        // term freq
//        Map<String, Integer> tf = new HashMap<>();
//        for (String t : tokens) {
//            tf.merge(t, 1, Integer::sum);
//        }
//
//        for (Map.Entry<String, Integer> e : tf.entrySet()) {
//            Integer idx = index.get(e.getKey());
//            if (idx == null) continue;
//            double tfw = 1.0 + Math.log(e.getValue());    // log TF
//            double idfw = idf.getOrDefault(e.getKey(), 1.0);
//            vec[idx] = tfw * idfw;
//        }
//        return vec;
//    }
//
//    // 코사인 유사도
//    public static double cosineSimilarity(double[] a, double[] b) {
//        double dot = 0.0, na = 0.0, nb = 0.0;
//        for (int i = 0; i < a.length; i++) {
//            dot += a[i] * b[i];
//            na += a[i] * a[i];
//            nb += b[i] * b[i];
//        }
//        return (na == 0 || nb == 0) ? 0.0 : dot / (Math.sqrt(na) * Math.sqrt(nb));
//    }
//
//    ///////////////
//
//    /** 사용 예) double s = CardNameSim.similarity("신한 라이킷 FUN+ 체크카드", "신한 likit fun plus 카드"); */
//    public static double similarity(String a, String b) {
//        // 1) 정규화
//        String na = normalize(a);
//        String nb = normalize(b);
//
//        // 2) 문자 n-gram (보통 3-gram)
//        int n = 3;
//        List<String> ga = charNGrams(na, n);
//        List<String> gb = charNGrams(nb, n);
//
//        // 3) 두 문서만의 DF/IDF (N=2, smoothing)
//        Map<String, Integer> df = new HashMap<>();
//        dfCount(df, ga);
//        dfCount(df, gb);
//        int vocabSize = df.size();
//        if (vocabSize == 0) return (na.isEmpty() && nb.isEmpty()) ? 1.0 : 0.0;
//
//        Map<String, Integer> index = new HashMap<>(vocabSize * 2);
//        int idx = 0;
//        for (String g : df.keySet()) index.put(g, idx++);
//        double[] idf = new double[vocabSize];
//        int N = 2;
//        for (Map.Entry<String,Integer> e : df.entrySet()) {
//            int i = index.get(e.getKey());
//            idf[i] = Math.log((N + 1.0) / (e.getValue() + 1.0)) + 1.0;
//        }
//
//        // 4) TF‑IDF(vec a/b, 희소)
//        Map<Integer, Double> va = tfidfSparse(ga, index, idf);
//        Map<Integer, Double> vb = tfidfSparse(gb, index, idf);
//
//        // 5) 코사인(희소)
//        return cosineSparse(va, vb);
//    }
//
//    /** 카드명 정규화: NFKC, 소문자, 기호 정리, plus/로마숫자 치환, 다중공백/불용어 제거 */
//    private static String normalize(String s) {
//        if (s == null) return "";
//        String x = Normalizer.normalize(s, Normalizer.Form.NFKC)
//                .replace('\u00A0', ' ')
//                .toLowerCase(Locale.ROOT);
//
//        x = x.replaceAll("[+]+"," plus ");
//        x = x.replaceAll("[·•∙⋅·]", " ");
//        x = x.replaceAll("[^0-9a-zA-Z가-힣\\s]", " ");
//
//        // 간단 로마숫자
//        x = x.replaceAll("\\biii\\b","3")
//                .replaceAll("\\bii\\b","2")
//                .replaceAll("\\biv\\b","4")
//                .replaceAll("\\bv\\b","5")
//                .replaceAll("\\bvi\\b","6");
//
//        x = x.replaceAll("\\s+"," ").trim();
//
//        Set<String> STOP = new HashSet<>(Arrays.asList(
//                "카드","신용","체크","플래티넘","클래식","에디션","edition","card","credit","check","the","of"
//        ));
//
//        if (x.isEmpty()) return x;
//        List<String> kept = new ArrayList<>();
//        for (String t : x.split(" ")) if (!STOP.contains(t)) kept.add(t);
//        return String.join(" ", kept);
//    }
//
//    /** 완전일치 체크용: 공백/기호 제거 후 비교 */
//    public static String normalizeForExact(String s) {
//        if (s == null) return "";
//        return Normalizer.normalize(s, Normalizer.Form.NFKC)
//                .replaceAll("\\s+","")
//                .replaceAll("[^0-9A-Za-z가-힣]", "")
//                .toLowerCase(Locale.ROOT);
//    }
//
//    private static List<String> charNGrams(String s, int n) {
//        List<String> out = new ArrayList<>();
//        if (s == null || s.isEmpty()) return out;
//        String x = " " + s + " ";
//        for (int i = 0; i <= x.length() - n; i++) {
//            String g = x.substring(i, i + n);
//            if (!g.trim().isEmpty()) out.add(g);
//        }
//        return out;
//    }
//    private static void dfCount(Map<String,Integer> df, List<String> grams) {
//        if (grams.isEmpty()) return;
//        Set<String> uniq = new HashSet<>(grams);
//        for (String g : uniq) df.merge(g, 1, Integer::sum);
//    }
//    private static Map<Integer, Double> tfidfSparse(List<String> grams, Map<String,Integer> index, double[] idf) {
//        Map<Integer, Integer> tf = new HashMap<>();
//        for (String g : grams) {
//            Integer i = index.get(g);
//            if (i != null) tf.merge(i, 1, Integer::sum);
//        }
//        Map<Integer, Double> w = new HashMap<>(tf.size() * 2);
//        for (Map.Entry<Integer,Integer> e : tf.entrySet()) {
//            double tfw = 1.0 + Math.log(e.getValue());
//            w.put(e.getKey(), tfw * idf[e.getKey()]);
//        }
//        return w;
//    }
//    private static double cosineSparse(Map<Integer, Double> a, Map<Integer, Double> b) {
//        if (a.isEmpty() && b.isEmpty()) return 1.0;
//        if (a.isEmpty() || b.isEmpty()) return 0.0;
//
//        Map<Integer, Double> small = a.size() <= b.size() ? a : b;
//        Map<Integer, Double> large = a.size() <= b.size() ? b : a;
//
//        double dot = 0.0, na = 0.0, nb = 0.0;
//        for (Map.Entry<Integer, Double> e : small.entrySet()) {
//            double va = e.getValue();
//            double vb = large.getOrDefault(e.getKey(), 0.0);
//            dot += va * vb;
//        }
//        for (double va : a.values()) na += Math.pow(va, 2);
//        for (double vb : b.values()) nb += Math.pow(vb, 2);
//
//        return (na == 0.0 || nb == 0.0) ? 0.0 : dot / (Math.sqrt(na) * Math.sqrt(nb));
//    }
//
//
//
//}
//
