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

    public static List<String> tokenizeAndClean(String text) {
        // 정규화 (오타 및 반복 문자 정리)
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

        // 형태소 분석 (Tokenize)
        List<String> tokens = OpenKoreanTextProcessorJava.tokensToJavaStringList(
                OpenKoreanTextProcessorJava.tokenize(normalized)
        );

        // 불용어 제거
        return tokens.stream()
                .filter(token -> !stopWords.contains(token))
                .collect(Collectors.toList());
    }
}

