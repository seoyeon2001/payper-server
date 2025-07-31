package com.payper.external.openai;

import com.fasterxml.jackson.core.JsonGenerator;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Log4j2
public class OpenAISqlGenerator {
    private static final String API_KEY = GptConfig.getApiKey();
    private static final String API_URL = GptConfig.getApiUrl();

    public static String generateSql(String title, String summary, String description) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

        try {
            String prompt = String.format("""
            다음은 카드 혜택 정보입니다.
            title: %s
            summary: %s
            description: %s

            위 정보를 기반으로 categories 배열과 Discount 객체를 추출해서 JSON으로 출력해줘.
            Discount 필드: type(RATE|AMOUNT), amount, minPayment, limitAmount, limitCount
            """, title, summary, description);

            String escapedPrompt = mapper.writeValueAsString(prompt);

            String requestBody = """
            {
              "model": "gpt-4",
              "messages": [
                { "role": "system", "content": "카드 혜택 정보를 분석해 categories와 Discount 객체를 JSON으로 추출하는 역할입니다." },
                { "role": "user", "content": %s }
              ],
              "temperature": 0.5
            }
            """.formatted(escapedPrompt);

            RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    return new BufferedReader(
                            new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8)
                    ).lines().reduce("", (a, b) -> a + b);
                }
            }
        } catch (Exception e) {
            log.error("GPT 요청 실패: {}", e.getMessage());
        }

        return null;
    }

}
