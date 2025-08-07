package com.payper.external.crawling.service.sub;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.domain.category.CategoryMapper;
import com.payper.domain.category.domain.Category;
import com.payper.domain.partner.PartnerMapper;
import com.payper.domain.partner.domain.Partner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIExtractPartnerAndCategoryService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final PartnerMapper partnerMapper;
    private final CategoryMapper categoryMapper;

    public static class Result {
        public List<String> partners;
        public List<String> categories;
    }

    public Result extract(String fullText) {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

        // 등록된 가맹점 / 카테고리 리스트
        List<Partner> partnerList = partnerMapper.selectAll();
        List<String> knownPartners = new ArrayList<>();
        for (Partner partner : partnerList) {
            knownPartners.add(partner.getPartnerName());
        }

        List<Category> categoryList = categoryMapper.selectAll();
        List<String> knownCategories = new ArrayList<>();
        for (Category category : categoryList) {
            knownCategories.add(category.getCategoryName());
        }

        try {
            String prompt = """
        다음은 카드 혜택에 대한 정보이다.

        너는 아래 텍스트에서 '등록된 가맹점 리스트'와 '등록된 카테고리 리스트'에 포함된 항목 중
        실제로 언급되었거나, 의미상 동일한 것으로 추정되는 항목만 추출해야 해.

        - 단어가 정확히 일치하지 않아도, 일반적으로 해당 브랜드나 카테고리를 지칭하는 표현이면 포함해도 돼.
        - 하지만, 내가 제공한 리스트에 없는 항목은 절대 포함하지 마.
        - 결과는 JSON 형식으로: {"partners": ["CU", "투썸플레이스"], "categories": ["편의점", "카페"]}
        - 아무것도 없으면 null 반환

        등록된 가맹점 리스트:
        %s

        등록된 카테고리 리스트:
        %s

        텍스트 (title, summary, description 통합):
        %s
        """.formatted(
                    mapper.writeValueAsString(knownPartners),
                    mapper.writeValueAsString(knownCategories),
                    fullText
            );


            String escapedPrompt = mapper.writeValueAsString(prompt);

            String requestBody = String.format("""
            {
              "model": "gpt-4",
              "messages": [
                { "role": "system", "content": "가맹점명과 카테고리명을 JSON 형식으로 추출하는 작업입니다." },
                { "role": "user", "content": %s }
              ],
              "temperature": 0.3
            }
            """, escapedPrompt);

            RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    String responseText = new BufferedReader(
                            new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8)
                    ).lines().reduce("", (a, b) -> a + b);

                    log.info("GPT 응답: {}", responseText);

                    JsonNode root = mapper.readTree(responseText);

                    if (root.has("error")) {
                        log.error("OpenAI API 오류: {}", root.path("error").path("message").asText());
                        return null;
                    }

                    String content = root.path("choices").get(0).path("message").path("content").asText();
                    if ("null".equals(content.trim())) return new Result();

                    JsonNode contentNode = mapper.readTree(content);
                    Result result = new Result();
                    result.partners = contentNode.has("partners")
                            ? mapper.convertValue(contentNode.get("partners"), new TypeReference<>() {})
                            : Collections.emptyList();
                    result.categories = contentNode.has("categories")
                            ? mapper.convertValue(contentNode.get("categories"), new TypeReference<>() {})
                            : Collections.emptyList();

                    return result;
                }
            }
        } catch (Exception e) {
            log.error("GPT 요청 실패: {}", e.getMessage(), e);
        }

        return null;
    }
}
