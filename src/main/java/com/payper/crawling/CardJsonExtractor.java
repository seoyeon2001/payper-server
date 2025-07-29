package com.payper.crawling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.crawling.dto.Benefit;
import com.payper.crawling.dto.CardData;

import java.util.ArrayList;

public class CardJsonExtractor {

    public static CardData parseCardJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        CardData data = new CardData();

        JsonNode card = root.path("pageProps")
                .path("dehydratedState")
                .path("queries").get(0)
                .path("state")
                .path("data")
                .path("card")
                .path("card");

        data.cardName = card.path("name").asText("");
        data.imageUrl = card.path("imageUrl").asText("");

        // 카드사
        JsonNode organization = card.path("organization");
        data.companyName = organization.path("name").asText("");

        // 카드 타입
        String cardTypeEnum = card.path("cardTypeEnum").asText("");
        if (cardTypeEnum.startsWith("CARD_TYPE_")) {
            data.cardType = cardTypeEnum.substring("CARD_TYPE_".length());
        } else {
            data.cardType = "UNKNOWN";
        }

        // 발급 URL
        JsonNode issueUrls = card.path("issueUrls");
        if (issueUrls.isArray()) {
            for (JsonNode issue : issueUrls) {
                if ("CARD_ISSUE_CHANNEL_WEB".equals(issue.path("channel").asText())) {
                    data.issueUrl = issue.path("url").asText("");
                    break;
                }
            }
        }

        data.benefits = new ArrayList<>();
        JsonNode steps = card.path("chartBenefits");
        if (steps.isArray()) {
            for (JsonNode step : steps) {
                Benefit benefit = new Benefit();
                benefit.title = step.path("title").asText("");
                benefit.summary = step.path("summary").asText("");
                benefit.description = step.path("description").asText("");

                data.benefits.add(benefit);
            }
        }

        return data;
    }

//    // test
//    public static void main(String[] args) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(new File("C:/Users/keji1/IdeaProjects/payper-devs/payper-server/CARD004231.json"));
//        String json = mapper.writeValueAsString(root);
//
//        CardData data = parseCardJson(json);
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
//    }
}
