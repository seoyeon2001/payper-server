package com.payper.external.crawling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.external.crawling.dto.CardData;
import com.payper.external.crawling.service.sub.CardBenefitCleanService;
import com.payper.external.crawling.service.sub.CardGradeCleanService;
import com.payper.external.crawling.service.sub.CardJsonExtractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlingService {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    private final CardJsonExtractService cardJsonExtractService;
    private final CardBenefitCleanService cardBenefitCleanService;
    private final CardGradeCleanService cardGradeCleanService;

    @Transactional
    public List<CardData> crawlsCards() {
        List<CardData> result = new ArrayList<>();

        int startId = 2423; //임시 test
        for (int id = startId; id <= startId; id++) {
            try {
                String url = "https://api.card-gorilla.com:8080/v1/cards/" + id;
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "Mozilla/5.0")
                        .build();
                Response response = okHttpClient.newCall(request).execute();

                if (!response.isSuccessful()) {
                    log.info("ID {} 요청 실패 : {}", id, response.code());
                    continue;
                }

                String body = response.body().string();
                CardData data = cardJsonExtractService.parseCardJson(body);
                log.info("ID {} 유효", id);

                // 등급 정제 후 저장
                var gradeResult = cardGradeCleanService.cleanGrade(data.getGradeDescription());
                data.setGrades(gradeResult);

                // 혜택 정제 후 저장
//                for (Benefit b : data.getBenefits()) {
//                    var benefitResult = cardBenefitCleanService.cleanBenefit(
//                            b.getSummary(),
//                            b.getDescription()
//                    );
//
//                    b.setCategories(benefitResult.getCategories());
//                    b.setDiscount(benefitResult.getDiscount());
//                }

                result.add(data);

            } catch (Exception e) {
                log.warn("ID {} 크롤링 실패: {}", id, e.getMessage());
            }
        }
        return result;
    }

}
