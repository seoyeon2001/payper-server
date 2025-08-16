package com.payper.external.codef.util;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;

import java.util.List;
import java.util.stream.Collectors;

// 형태소 분석 및 전처리
public class KoreanTokenizer {
    // 제거할 불용어
    /**
     * 카드: 의미있는 단어가 아님 - 공통으로 들어감
     * 신용 / 체크: type에서 신용, 체크를 먼저 확인한다면 의미있는 값이 아님
     */
    private static final List<String> stopWords = List.of("카드", "신용", "체크", "기본", "클래식");

    private static final List<String> companyNames = List.of(
            "국민", "국민은행", "국민카드", "KB", "KB국민", "KB국민은행", "KB국민카드",
            "신한", "신한은행", "신한카드", "우리", "우리은행", "우리카드", "하나","하나은행", "하나카드",
            "롯데", "롯데카드", "삼성", "삼성카드", "현대", "현대카드", "농협", "농협은행", "NH", "NH농협",
            "카카오", "카카오뱅크", "카카오페이", "토스", "토스뱅크", "토스페이",
            "씨티", "씨티카드", "광주은행", "전북은행", "제주은행", "수협", "수협은행", "SH수협은행",
            "기업은행", "IBK", "IBK은행", "IBK기업은행", "BC", "BC카드", "BC바로", "BC바로카드"
    );

    public static List<String> tokenizeAndClean(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        // 정규화 (오타 및 반복 문자 정리)
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

        // 특수문자 제거 (공백으로 치환)
        String cleaned = normalized.toString().replaceAll("[^ㄱ-ㅎ가-힣a-zA-Z0-9\\s]", " ");

        // 카드사 및 은행명 제거
        for (String company : companyNames) {
            cleaned = cleaned.replaceAll("(?i)" + company, " "); // 대소문자 무시
        }

        // 공백 정리
        cleaned = cleaned.replaceAll("\\s+", " ").trim();


        // 형태소 분석 (Tokenize)
        List<String> tokens = OpenKoreanTextProcessorJava.tokensToJavaStringList(
                OpenKoreanTextProcessorJava.tokenize(cleaned)
        );

        // 불용어 제거
        return tokens.stream()
                .filter(token -> !token.isBlank() && !stopWords.contains(token))
                .filter(token -> !stopWords.contains(token))
                .collect(Collectors.toList());
    }


}

