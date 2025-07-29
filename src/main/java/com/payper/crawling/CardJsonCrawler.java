package com.payper.crawling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.crawling.dto.CardData;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.payper.crawling.CardJsonExtractor.parseCardJson;


@Log4j2
public class CardJsonCrawler {
    public static void main(String[] args){
        ObjectMapper mapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();

        String baseUrl = "https://api.card-gorilla.com:8080/v1/cards/";

        for (int cardId = 1; cardId <= 2; cardId++) { //test용
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
                    // 여기서 data를 DB insert 로직 또는 LLM 요청에 넘기면 됨.
            } catch (Exception e) {
                log.info("ID {} 예외 발생 : {}", cardId, e.getMessage());
            }
        }
    }
}
