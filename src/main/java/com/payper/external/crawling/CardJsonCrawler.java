package com.payper.external.crawling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.external.crawling.dto.Benefit;
import com.payper.external.crawling.dto.CardData;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.payper.external.crawling.CardJsonExtractor.parseCardJson;

@Log4j2
public class CardJsonCrawler {
    public static void main(String[] args){
        ObjectMapper mapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();

        String baseUrl = "https://api.card-gorilla.com:8080/v1/cards/";

        for (int cardId = 2422; cardId <= 2422; cardId++) { //test용
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
                log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));

                for (int i = 0; i < data.getBenefits().size(); i++) {
                    Benefit benefit = data.getBenefits().get(i);

                    CardBenefitCleaner.CleanedResult cleaned = CardBenefitCleaner.clean(
                            benefit.getTitle(),
                            benefit.getSummary(),
                            benefit.getDescription()
                    );
                    CardBenefitCleaner.Discount d = cleaned.getDiscount();
                    log.info("{} 할인 정보 - type: {}, amount: {}, minPayment: {}, limitAmount: {}, limitCount: {}",
                            i+1, d.getType(), d.getAmount(), d.getMinPayment(), d.getLimitAmount(), d.getLimitCount());
                    CardBenefitCleaner.Grade g = cleaned.getGrade();
                    log.info("{} 실적 정보 - start: {}, end: {}, totaldiscount: {}",
                            i+1, g.getStart(), g.getEnd(), g.getTotalDiscount());


//                    String result = OpenAISqlGenerator.generateSql(
//                            benefit.getTitle(),
//                            benefit.getSummary(),
//                            benefit.getDescription(),
//                            cleaned.getCategories(),
//                            cleaned.getDiscount()
//                    );

//                    log.info("✅ 카드 [{}] - 혜택[{}] 결과:\n{}", data.getCardName(), i + 1, result);
                }
            } catch (Exception e) {
                log.info("ID {} 예외 발생 : {}", cardId, e.getMessage());
            }
        }
    }
}
