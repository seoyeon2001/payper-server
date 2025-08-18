package com.payper.external.gpt;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.domain.user.dto.UserReportResponse;
import com.payper.domain.user.dto.UserTransactionSummaryDto;
import com.payper.external.gpt.exception.OpenAIAnalyseReportException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIExtractService {
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.model:gpt-4o-mini-2024-07-18}")
    private String model;

    /** ==== CONFIG ==== */
    private static final int MAX_ITEMS_PER_CALL = 50;
    private static final List<String> ALLOWED = List.of(
            "카페/디저트", "베이커리", "편의점", "마트/슈퍼",
            "한식", "중식", "일식", "양식", "분식", "패스트푸드", "배달", "일반음식점", "술/유흥",
            "버스/지하철", "택시/모빌리티",
            "쇼핑", "온라인쇼핑", "영화/공연", "게임/PC방", "여행/숙박", "스포츠", "약국/병원", "뷰티",
            "간편결제", "기타"
    );

    /** ==== PUBLIC ==== */
    public UserReportResponse extract(List<UserTransactionSummaryDto> rows, String startDate, String endDate) {
        objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

        if (rows.isEmpty()) {
            return UserReportResponse.ofDto(startDate, endDate, "무소비의 전설", List.of());
        }

        // 입력 전처리
        record Item(Integer id, String store, String type) { }
        List<Item> items = new ArrayList<>();
        for (UserTransactionSummaryDto r : rows) {
            Integer id = r.getUserCardTransactionId();
            String store = r.getResMemberStoreName();
            String type = r.getResMemberStoreType();

            if (id == null || store == null || store.isBlank()) continue;
            items.add(new Item(id, store, type));
        }

        try {
            // 프롬프트/스키마 준비
            String instructions = buildInstructions();
            Map<String, Object> schema = buildSchema();

            // 청크 호출
            List<List<Item>> parts = chunk(items);
            Set<Integer> allowIds = items.stream().map(Item::id)
                    .collect(java.util.stream.Collectors.toSet());

            List<JsonNode> payloads = new ArrayList<>();
            for (List<Item> part : parts) {
                JsonNode payload = callOpenAIOnce(instructions, schema, part);
                payloads.add(payload);
            }

            // 병합
            JsonNode merged = (payloads.size() == 1)
                    ? payloads.get(0)
                    : mergePayloads(payloads, allowIds);

            // 합산 및 정렬 -> 응답
            return buildReportFromBuckets(merged, rows, startDate, endDate);

        } catch (Exception e) {
            log.error("GPT 분석 실패: {}", e.getMessage(), e);
            throw new OpenAIAnalyseReportException();
        }
    }

    private static String buildInstructions() {
        return """
                    너는 카드 사용내역을 한국 로컬 카테고리로 묶는 분류기다.
                    - 입력 items는 [{ id, store, type }] 이다. id는 정수형 실제 거래 ID다.
                    - 출력은 반드시 JSON (스키마 고정):
                      { "title": "10자 내외 한국어 우스꽝스러운 별명 (예: '카페인 뱀파이어')",
                        "buckets": [ { "category": "<카테고리>", "ids": [정수 ID들] }, ... ] }
                    - 제약:
                      1) ids에는 오직 입력 items의 id만 사용 (새 숫자 생성 금지)
                      2) 모든 id는 정확히 한 번만 등장 (중복/누락 금지)
                      3) store를 모르면, type으로 구분해라.
                      4) 카테고리는 아래 집합만 사용: %s
                """.formatted(ALLOWED);
    }

    private static Map<String, Object> buildSchema() {
        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "required", List.of("buckets", "title"),
                "properties", Map.of(
                        "title", Map.of("type", "string", "maxLength", 20),
                        "buckets", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "object",
                                        "additionalProperties", false,
                                        "required", List.of("category", "ids"),
                                        "properties", Map.of(
                                                "category", Map.of("type", "string", "enum", ALLOWED),
                                                "ids", Map.of(
                                                        "type", "array",
                                                        "items", Map.of("type", "integer")
                                                )
                                        )
                                )
                        )
                )
        );
    }

    // 청크 호출
    private static <T> List<List<T>> chunk(List<T> list) {
        if (list == null || list.isEmpty()) return List.of();
        if (list.size() <= MAX_ITEMS_PER_CALL) return List.of(list);

        List<List<T>> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i += MAX_ITEMS_PER_CALL) {
            out.add(list.subList(i, Math.min(i + MAX_ITEMS_PER_CALL, list.size())));
        }
        return out;
    }

    // OpenAI 1회 호출 → payload(JsonNode) 반환
    private JsonNode callOpenAIOnce(String instructions,
                                    Map<String, Object> schema,
                                    Object partItemsJson) throws Exception {
        Map<String, Object> req = new LinkedHashMap<>();
        req.put("model", model);
        req.put("temperature", 0);
        req.put("instructions", instructions);
        req.put("input", "items:\n" + objectMapper.writeValueAsString(partItemsJson));
        req.put("max_output_tokens", 400);
        req.put("text", Map.of(
                "format", Map.of(
                        "type", "json_schema",
                        "name", "PayperBucketsOnly",
                        "schema", schema,
                        "strict", true
                )
        ));

        String json = objectMapper.writeValueAsString(req);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response resp = okHttpClient.newCall(request).execute()) {
            String rid = resp.header("x-request-id");
            String respText = resp.body() != null
                    ? new String(resp.body().bytes(), StandardCharsets.UTF_8)
                    : "";

            if (!resp.isSuccessful()) {
                log.error("OpenAI API {} error (request-id: {}): {}", resp.code(), rid, respText);
                throw new RuntimeException("OpenAI API 호출 실패: " + resp.code());
            }

            JsonNode root = objectMapper.readTree(respText);
            String textJson = root.path("output").get(0).path("content").get(0).path("text").asText();

            JsonNode payload = objectMapper.readTree(textJson);
            log.info("[OpenAI decoded][rid={}] {}", rid, payload.toPrettyString());
            return payload;
        }
    }

    private JsonNode mergePayloads(List<JsonNode> payloads, Set<Integer> allowIds) {
        Map<String, LinkedHashSet<Integer>> acc = new LinkedHashMap<>();

        // 여러 payload → 카테고리별 ID 병합
        for (JsonNode payload : payloads) {
            JsonNode buckets = payload.path("buckets");
            if (!buckets.isArray()) continue;

            for (JsonNode b : buckets) {
                String cat = b.path("category").asText("기타");
                JsonNode arr = b.path("ids");
                if (!arr.isArray()) continue;

                var set = acc.computeIfAbsent(cat, k -> new LinkedHashSet<>());
                for (JsonNode v : arr) {
                    if (!v.canConvertToInt()) continue;
                    int id = v.asInt();
                    if (allowIds == null || allowIds.contains(id)) set.add(id); // 청크 간 중복 자동 제거
                }
            }
        }

        // 누락 보완: missing = allow - mergedAll → '기타'로 편성
        if (allowIds != null) {
            Set<Integer> mergedAll = new HashSet<>();
            for (Set<Integer> set : acc.values()) mergedAll.addAll(set);

            Set<Integer> missing = new HashSet<>(allowIds);
            missing.removeAll(mergedAll);

            if (!missing.isEmpty()) acc.computeIfAbsent("기타", k -> new LinkedHashSet<>()).addAll(missing);
        }

        // 최종 JsonNode로 반환
        record Bucket(String category, List<Integer> ids) {}
        record Merged(String title, List<Bucket> buckets) {}

        String title = randomTitleFromPayloads(payloads);

        List<Bucket> buckets = new ArrayList<>();
        for (Map.Entry<String, LinkedHashSet<Integer>> e : acc.entrySet()) {
            buckets.add(new Bucket(e.getKey(), new ArrayList<>(e.getValue())));
        }

        return objectMapper.valueToTree(new Merged(title, buckets));
    }

    // 청크들이 준 title 중 랜덤 선택 (없으면 기본값)
    private static String randomTitleFromPayloads(List<JsonNode> payloads) {
        List<String> titles = new ArrayList<>();
        for (JsonNode p : payloads) {
            String t = p.path("title").asText(null);
            if (t == null) continue;

            t = t.replaceAll("[`\"\\n\\r]", "").trim();
            if (!t.isBlank()) titles.add(t);
        }

        if (titles.isEmpty()) return "소비 마스터";

        return titles.get(ThreadLocalRandom.current().nextInt(titles.size()));
    }

    // GPT가 준 분류 결과(JSON), 원본 거래 rows -> 카테고리별 금액 합계 리포트
    private UserReportResponse buildReportFromBuckets(JsonNode textJson, List<UserTransactionSummaryDto> rows, String startDate, String endDate) {

        Map<Integer, Long> amountById = new HashMap<>();
        for (UserTransactionSummaryDto r : rows) {
            Integer id = r.getUserCardTransactionId();
            long amt = Long.parseLong(r.getResUsedAmount());
            if (amt > 0) amountById.put(id, amt);
        }

        // 카테고리별 합계 + 중복/누락 방어
        Map<String, Long> sums = new HashMap<>();
        Set<Integer> seen = new HashSet<>();

        JsonNode buckets = textJson.path("buckets");
        if (buckets.isArray()) {
            for (JsonNode b : buckets) {
                String cat = b.path("category").asText("기타");
                long catSum = 0L;

                JsonNode arr = b.path("ids");
                if (arr.isArray()) {
                    for (JsonNode v : arr) {
                        if (!v.canConvertToInt()) continue;
                        int id = v.asInt();

                        // 동일 ID가 여러 카테고리에 들어오면 최초 1회만 집계
                        if (seen.add(id)) {
                            catSum += amountById.getOrDefault(id, 0L);
                        } else {
                            log.warn("중복 ID 감지로 제외됨: {}", id);
                        }
                    }
                }
                if (catSum > 0) sums.merge(cat, catSum, Long::sum);
            }
        }

        // 정렬하여 응답 구성
        List<Map.Entry<String, Long>> entries = new ArrayList<>(sums.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        List<UserReportResponse.ReportSummary> summaries = new ArrayList<>(entries.size());
        for (Map.Entry<String, Long> e : entries) {
            UserReportResponse.ReportSummary rs = new UserReportResponse.ReportSummary();
            rs.setCategory(e.getKey());
            rs.setAmount(e.getValue());

            summaries.add(rs);
        }

        String alias = textJson.path("title").asText("소비왕");
        return UserReportResponse.ofDto(startDate, endDate, alias, summaries);
    }
}
