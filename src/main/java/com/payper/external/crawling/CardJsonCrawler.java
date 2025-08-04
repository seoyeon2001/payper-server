package com.payper.external.crawling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.external.crawling.dto.Benefit;
import com.payper.external.crawling.dto.CardData;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.payper.external.crawling.CardBenefitCleaner.cleanBenefit;
import static com.payper.external.crawling.CardGradeCleaner.cleanGrade;
import static com.payper.external.crawling.CardJsonExtractor.parseCardJson;

@Slf4j
public class CardJsonCrawler {
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

                CardData data = parseCardJson(body);
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


//                    String result = OpenAISqlGenerator.generateSql(
//                            benefit.getTitle(),
//                            benefit.getSummary(),
//                            benefit.getDescription(),
//                            cleaned.getCategories(),
//                            cleaned.getDiscount()
//                    );

//                   log.info(" 카드 [{}] - 혜택[{}] 결과:\n{} \n", data.getCardName(), i + 1, result);
                }
            } catch (Exception e) {
               log.info("ID {} 예외 발생 : {}", cardId, e.getMessage());
            }
        }
    }
}
