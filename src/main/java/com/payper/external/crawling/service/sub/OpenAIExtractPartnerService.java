package com.payper.external.crawling.service.sub;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class OpenAIExtractPartnerService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    public List<String> extractPartners(String summary, String description) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

        try {
            String prompt =
                    "다음 텍스트에서 브랜드명 또는 가맹점명만 정확히 추출해줘.\n" +
                            "- 할인/적립/청구할인/캐시백 등의 혜택 설명은 제거\n" +
                            "- '버스/지하철' 같이 의미상 묶인 표현은 분리하지 말 것\n" +
                            "- 일반적으로 잘 알려진 브랜드/프랜차이즈/편의점/마트/커피숍/음식점/온라인 쇼핑몰 등만 포함\n" +
                            "- '[오프라인 백화점] 롯데, 현대, 신세계 백화점'처럼 공통된 접미사(예: 백화점)가 앞에 있으면, 뒤의 브랜드명에도 같은 접미사를 붙여서 완성된 이름으로 추출 (예: \"롯데백화점\", \"현대백화점\")\n" +
                            "- 가맹점이 포함되지 않거나 알 수 없는 경우에는 null을 반환\n" +
                            "- 결과는 JSON으로 출력 (예: [\"스타벅스\", \"커피빈\"] 또는 null)\n\n" +
                            "입력 예시: \"[일상] 스타벅스, 커피빈 10% 할인\"\n" +
                            "출력 예시: [\"스타벅스\", \"커피빈\"]\n\n" +
                            "입력 예시: \"본 상품은 카드사에서 제공하는 부가서비스가 없습니다.\"\n" +
                            "출력 예시: null\n\n" +
                            "입력: \"" + summary + description + "\"\n출력:";


            String escapedPrompt = mapper.writeValueAsString(prompt); // JSON용 이스케이프

            String requestBody = String.format("""
            {
              "model": "gpt-4",
              "messages": [
                { "role": "system", "content": "브랜드명 또는 가맹점명만 정확하게 추출하는 태스크입니다. 결과는 JSON 배열로 반환하세요." },
                { "role": "user", "content": %s }
              ],
              "temperature": 0.5
            }
            """, escapedPrompt);


            RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            int maxRetries = 3;
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try (Response response = client.newCall(request).execute()) {
                    if (response.body() != null) {
                        String responseText = new BufferedReader(
                                new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8)
                        ).lines().reduce("", (a, b) -> a + b);

                        log.info("GPT 응답: {}", responseText);

                        JsonNode root = mapper.readTree(responseText);

                        if (root.has("error")) {
                            String errorType = root.path("error").path("type").asText();
                            if ("server_error".equals(errorType)) {
                                log.warn("서버 오류 발생. {}번째 재시도 중...", attempt);
                                Thread.sleep(1000); // 1초 대기 후 재시도
                                continue;
                            } else {
                                log.error("OpenAI API 오류: {}", root.path("error").path("message").asText());
                                break;
                            }
                        }

                        String content = root.path("choices").get(0).path("message").path("content").asText();

                        if (content.equals("null")) {
                            return Collections.emptyList();
                        }

                        try {
                            return mapper.readValue(content, new TypeReference<List<String>>() {});
                        } catch (Exception e) {
                            log.error("JSON 파싱 실패: {}", content);
                            return Collections.emptyList();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("GPT 요청 실패: {}", e.getMessage());
        }

        return null;
    }

}
