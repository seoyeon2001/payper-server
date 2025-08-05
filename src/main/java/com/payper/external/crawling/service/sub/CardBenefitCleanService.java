package com.payper.external.crawling.service.sub;

import com.payper.external.crawling.dto.Discount;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardBenefitCleanService {
    private final OpenAIExtractPartnerService openAIExtractPartnerService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CleanedResult {
        private List<String> categories;
        private Discount discount;
    }

    private static final Pattern PERCENT_PATTERN = Pattern.compile("(\\d+)%");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(\\d{1,3}(,\\d{3})+|\\d+)(?=원)");
    private static final Pattern MIN_PAYMENT_PATTERN = Pattern.compile("건당\\s*([\\d,]+(?:천|만)?)(?:원)?\\s*이상");;
    private static final Pattern LIMIT_AMOUNT_PATTERN = Pattern.compile("월 할인한도[^\\d]*(\\d{1,3}(,\\d{3})*|\\d+천)원");
    private static final Pattern LIMIT_COUNT_PATTERN = Pattern.compile("월 할인횟수[\\s:]*([0-9]+)회");
    private static final Pattern GRADE_START_PATTERN =
            Pattern.compile("전월 이용실적[^\\d]*(\\d{1,3}(?:,\\d{3})*|\\d+(?:천|만)?)(만원|천원)?");


    public CleanedResult cleanBenefit(String summary, String descriptionHtml) {
        String description = Jsoup.parse(descriptionHtml).text();

        List<String> partnerTargetText = openAIExtractPartnerService.extractPartners(summary);

        Long minPayment = description.contains("건당 이용조건 없음")
                ? 0L
                : extractLong(MIN_PAYMENT_PATTERN, description);

        Discount discount = Discount.builder()
                .type(getDiscountType(description))
                .amount(extractPercentOrAmount(description))
                .minPayment(minPayment)
                .limitAmount(extractLong(LIMIT_AMOUNT_PATTERN, description))
                .limitCount(extractLong(LIMIT_COUNT_PATTERN, description))
                .gradeStart(extractLong(GRADE_START_PATTERN, description))
                .build();
      
        return new CleanedResult(partnerTargetText, discount);
    }

    private String getDiscountType(String description) {
        if (description.contains("%")) return "RATE";
        else if (description.contains("원")) return "AMOUNT";
        return null;
    }

    private Long extractPercentOrAmount(String description) {
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

    private Long extractLong(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String matchedStr = matcher.group(1);
            String unit = matcher.groupCount() >= 2 ? matcher.group(2) : "";
            return parseAmount(matchedStr + (unit != null ? unit : ""));
        }
        return 0L;
    }


    private Long parseAmount(String str) {
        if (str == null) return null;

        str = str.replaceAll(",", "").replaceAll(" ", "").replaceAll("원", "");

        try {
            if (str.matches("\\d+천")) {
                return Long.parseLong(str.replace("천", "")) * 1000;
            } else if (str.matches("\\d+만")) {
                return Long.parseLong(str.replace("만", "")) * 10000;
            } else if (str.matches("\\d+")) {
                return Long.parseLong(str);
            }
        } catch (NumberFormatException e) {
            System.err.println("parseAmount 오류: " + str);
        }

        return null;
    }
}
