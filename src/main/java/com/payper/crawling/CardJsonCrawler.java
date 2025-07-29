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

        String baseUrl = "https://www.banksalad.com/_next/data/FyWzuXb7PkPehgVsR-3sk/product/cards/CARD%06d.json";

        for (int i = 1; i <= 2; i++) { //test용
            String cardId = String.format("CARD%06d", i);
            String url = String.format(baseUrl, i);

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

                if (!new org.json.JSONObject(body)
                        .getJSONObject("pageProps")
                        .optBoolean("shouldShowErrorPage", false)) {

                    CardData data = parseCardJson(body);
                    log.info("ID {} 유효", cardId);
                    log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
                    // 여기서 data를 DB insert 로직 또는 LLM 요청에 넘기면 됨

                } else {
                    log.info("ID {} 무효", cardId);
                }

            } catch (Exception e) {
                log.info("ID {} 예외 발생 : {}", cardId, e.getMessage());
            }
        }
    }
}
