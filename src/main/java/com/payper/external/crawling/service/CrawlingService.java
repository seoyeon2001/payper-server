package com.payper.external.crawling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.external.crawling.dto.Benefit;
import com.payper.external.crawling.dto.CardData;
import com.payper.external.crawling.service.sub.BenefitToCategoryAndPartnerService;
import com.payper.external.crawling.service.sub.CardJsonExtractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlingService {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    private final CardJsonExtractService cardJsonExtractService;
    private final BenefitToCategoryAndPartnerService benefitToCategoryAndPartnerService;

    @Transactional
    public CardData crawlsCards(int id) {
        try {
            String url = "https://api.card-gorilla.com:8080/v1/cards/" + id;
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("ID {} 요청 실패 : {}", id, response.code());
                    return null;
                }

                String body = response.body().string();
                CardData data = cardJsonExtractService.parseCardJson(body);
                log.info("ID {} 유효", id);

                // 혜택 정제 후 저장
                for (Benefit b : data.getBenefits()) {
                    var result = benefitToCategoryAndPartnerService.extract(
                            b.getTitle(),
                            b.getSummary(),
                            b.getDescription()
                    );

                    b.setCategoryIds(result.getCategoryIds());
                    b.setPartnerIds(result.getPartnerIds());
                }
                return data;
            }

        } catch (Exception e) {
            log.error("ID {} 크롤링 실패: {}", id, e.getMessage());
            return null;
        }
    }

}
