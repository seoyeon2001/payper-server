package com.payper.openai;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class OpenAISqlGenerator {
    private static final String API_KEY = GptConfig.getApiKey(); // 본인의 API 키
    private static final String API_URL = GptConfig.getApiUrl();

    public static void main(String[] args) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//        ObjectMapper mapper = new ObjectMapper();
//
//        String requestBody = """
//        {
//          "model": "gpt-4",
//          "messages": [
//            { "role": "system", "content": "카드 혜택 요약 insert문을 생성해줘." },
//            { "role": "user", "content": "‘스타벅스 10% 할인, 월 최대 3천원, 조건 없음’ 이 내용을 기반으로 insert문 생성해줘." }
//          ],
//          "temperature": 0.7
//        }
//        """;
//
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .header("Authorization", "Bearer " + API_KEY)
//                .header("Content-Type", "application/json")
//                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            System.out.println(response.body().string());
//        }
    }
}
