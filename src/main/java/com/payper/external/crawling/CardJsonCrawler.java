package com.payper.external.crawling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.external.crawling.dto.Benefit;
import com.payper.external.crawling.dto.CardData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.payper.external.crawling.CardBenefitCleaner.cleanBenefit;
import static com.payper.external.crawling.CardGradeCleaner.cleanGrade;
import static com.payper.external.crawling.CardJsonExtractor.parseCardJson;

@Slf4j
@RequiredArgsConstructor
public class CardJsonCrawler {
    // TODO: restcontroller 구현 후 해당 주석 풀어줘야 함
//    private final ObjectMapper objectMapper;
//    private final OkHttpClient client;
//    private final CardJsonExtractor cardJsonExtractor;

    public static void main(String[] args){
        ObjectMapper mapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();

        String baseUrl = "https://api.card-gorilla.com:8080/v1/cards/";

        int startId = 2749;
        for (int cardId = startId; cardId <= startId; cardId++) { //test용
            String url = baseUrl + cardId;

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "Mozilla/5.0")
                        .build();

                Response response = client.newCall(request).execute();
                String body = response.body().string();

                if (!response.isSuccessful()) {
                   log.info("ID {} 요청 실패 : {}", cardId, response.code());
                    continue;
                }

                // TODO: restcontroller 구현 후 해당 코드 삭제 해야 함
                CardData data = parseCardJson(body);

                // TODO: restcontroller 구현 후 해당 주석 풀어줘야 함
//                CardData data = cardJsonExtractor.parseCardJson(body);
               log.info("ID {} 유효", cardId);
                System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));

                for (int i = 0; i < data.getBenefits().size(); i++) {
                    Benefit benefit = data.getBenefits().get(i);

                    CardBenefitCleaner.CleanedResult cleaned = cleanBenefit(
                            benefit.getSummary(),
                            benefit.getDescription()
                    );

                    CardGradeCleaner.CleanedGradeResult cleanedGrade = cleanGrade(
                            data.getGradeDescription()
                    );

                    log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cleaned));
                    log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cleanedGrade));

                }
            } catch (Exception e) {
               log.info("ID {} 예외 발생 : {}", cardId, e.getMessage());
            }
        }
    }
}
