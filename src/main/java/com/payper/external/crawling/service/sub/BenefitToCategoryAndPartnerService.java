package com.payper.external.crawling.service.sub;

import com.payper.domain.category.CategoryMapper;
import com.payper.domain.category.domain.Category;
import com.payper.domain.partner.PartnerMapper;
import com.payper.domain.partner.domain.Partner;
import com.payper.external.crawling.config.partnerSynonyms;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BenefitToCategoryAndPartnerService {
    private final CategoryMapper categoryMapper;
    private final PartnerMapper partnerMapper;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private Set<Integer> categoryIds;
        private Set<Integer> partnerIds;
    }

    private static final List<String> exclusionKeywords = List.of(
            "할인제외", "제외", "유의사항", "실적 제외", "않", "없"
    );

    public Result extract(String title, String summary, String descriptionHtml) {
        if ("유의사항".equals(title)) {
            return new Result(new HashSet<>(), new HashSet<>());
        }

        StringBuilder sb = new StringBuilder();
        if (title != null) sb.append(title).append(" ");
        if (summary != null) sb.append(summary).append(" ");
        if (descriptionHtml != null) sb.append(Jsoup.parse(descriptionHtml).text());

        String fullText = sb.toString();
        String searchTargetText = removeExclusionSections(fullText).toLowerCase();

        List<Category> categories = categoryMapper.selectAll();
        List<Partner> partners = partnerMapper.selectAll();

        Set<Integer> matchedCategoryIds = new HashSet<>();
        Set<Integer> matchedPartnerIds = new HashSet<>();

        for (Partner partner : partners) {
            if (isSimilar(searchTargetText, partner.getPartnerName())) {
                matchedPartnerIds.add(partner.getPartnerId());
                matchedCategoryIds.add(partner.getCategoryId());
            }
        }

        for (Category category : categories) {
            if (searchTargetText.contains(category.getCategoryName())) {
                matchedCategoryIds.add(category.getCategoryId());
            }
        }

        return new Result(matchedCategoryIds, matchedPartnerIds);
    }

    private String removeExclusionSections(String fullText) {
        StringBuilder result = new StringBuilder();
        String[] lines = fullText.split("(?<=\\.)|(?=- )");

        for (String line : lines) {
            boolean isExclusionLine = false;
            for (String keyword : exclusionKeywords) {
                if (line.contains(keyword)) {
                    isExclusionLine = true;
                    break;
                }
            }
            if (!isExclusionLine) {
                result.append(line).append(" ");
            }
        }

        return result.toString();
    }

    // 유사어 포함 비교
    private boolean isSimilar(String text, String target) {
        text = text.toLowerCase();
        target = target.toLowerCase();

        if (text.contains(target)) return true;

        List<String> synonyms = partnerSynonyms.ps.getOrDefault(target, Collections.emptyList());
        for (String synonym : synonyms) {
            if (text.contains(synonym.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}