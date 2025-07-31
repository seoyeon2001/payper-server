package com.payper.crawling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardBenefitCleaner {

    private static final Pattern PERCENT_PATTERN = Pattern.compile("(\\d+)%");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(\\d{1,3}(,\\d{3})+|\\d+)(?=원)");
    private static final Pattern MIN_PAYMENT_PATTERN = Pattern.compile("건당\\s*([\\d,]+(?:천|만)?)(?:원)?\\s*이상");;
    private static final Pattern LIMIT_AMOUNT_PATTERN = Pattern.compile("월 할인한도[^\\d]*(\\d{1,3}(,\\d{3})*|\\d+천)원");
    private static final Pattern LIMIT_COUNT_PATTERN = Pattern.compile("월 할인횟수[\\s:]*([0-9]+)회");
    private static final Pattern START_PATTERN =
            Pattern.compile("전월 이용실적[^\\d]*(\\d{1,3}(?:,\\d{3})*|\\d+(?:천|만)?)(?:원)?");


    public static CleanedResult clean(String title, String summary, String descriptionHtml) {
        String description = Jsoup.parse(descriptionHtml).text();

        List<String> categories = new ArrayList<>(extractBrands(title + " " + summary + " " + description));

        Long minPayment = description.contains("건당 이용조건 없음")
                ? null
                : extractLong(MIN_PAYMENT_PATTERN, description);

        Discount discount = Discount.builder()
                .type(getDiscountType(description))
                .amount(extractPercentOrAmount(description))
                .minPayment(minPayment)
                .limitAmount(extractLong(LIMIT_AMOUNT_PATTERN, description))
                .limitCount(extractLong(LIMIT_COUNT_PATTERN, description))
                .build();


        Grade grade = Grade.builder()
                .start(extractLong(START_PATTERN, description))
                .end(null)
                .totalDiscount(null)
                .build();

        return new CleanedResult(categories, discount, grade);
    }

    private static String getDiscountType(String description) {
        if (description.contains("%")) return "RATE";
        else if (description.contains("원")) return "AMOUNT";
        return null;
    }

    private static Long extractPercentOrAmount(String description) {
        Matcher percent = PERCENT_PATTERN.matcher(description);
        if (percent.find()) {
            return Long.parseLong(percent.group(1));
        }
        Matcher amount = AMOUNT_PATTERN.matcher(description);
        if (amount.find()) {
            return parseAmount(amount.group(1));
        }
        return null;
    }

    private static Long extractLong(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String matchedStr = matcher.group(1);
            return parseAmount(matchedStr);
        }
        return null;
    }



    private static Long parseAmount(String str) {
        if (str == null) return null;

        str = str.replaceAll(",", "").replaceAll(" ", "").replaceAll("원", "");

        try {
            if (str.matches("\\d+천")) {
                return Long.parseLong(str.replace("천", "")) * 1000;
            } else if (str.matches("\\d+만")) {
                return Long.parseLong(str.replace("만", "")) * 10000;
            } else if (str.matches("\\d+")) {
                return Long.parseLong(str);
            } else if (str.matches("\\d+천원")) {
                return Long.parseLong(str.replace("천원", "")) * 1000;
            } else if (str.matches("\\d+만원")) {
                return Long.parseLong(str.replace("만원", "")) * 10000;
            }
        } catch (NumberFormatException e) {
            System.err.println("parseAmount 오류: " + str);
        }

        return null;
    }




    // 예시 키워드 기반 파트너 추출
    private static final List<String> BRAND_KEYWORDS = Arrays.asList(
            "스타벅스", "커피빈", "넷플릭스", "유튜브", "배달의민족", "요기요",
            "BHC", "교촌", "맥도날드", "버거킹", "카카오", "쿠팡", "11번가"
    );

    private static Set<String> extractBrands(String text) {
        Set<String> found = new LinkedHashSet<>();
        for (String brand : BRAND_KEYWORDS) {
            if (text.contains(brand)) {
                found.add(brand);
            }
        }
        return found;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Discount {
        private String type;
        private Long amount;
        private Long minPayment;
        private Long limitAmount;
        private Long limitCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Grade {
        private Long start;
        private Long end;
        private Long totalDiscount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CleanedResult {
        private List<String> categories;
        private Discount discount;
        private Grade grade;
    }
}
