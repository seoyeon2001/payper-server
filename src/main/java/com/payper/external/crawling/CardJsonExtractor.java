package com.payper.external.crawling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payper.external.crawling.dto.Benefit;
import com.payper.external.crawling.dto.CardData;

import java.util.ArrayList;

public class CardJsonExtractor {

    public static CardData parseCardJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        CardData data = new CardData();
        data.cardName = root.path("name").asText("");
        data.companyName = root.path("corp").path("name").asText("");
        data.cardType = MatchCardType(root.path("cate").asText(""));
        data.imageUrl = root.path("card_img").path("url").asText("");
        data.issueUrl = root.path("request_pc").asText(null);
        data.annualFee = root.path("annual_fee_basic").asText("");

        data.benefits = new ArrayList<>();
        JsonNode keyBenefits = root.path("key_benefit");
        if (keyBenefits.isArray()) {
            for (JsonNode keyBenefit : keyBenefits) {
                if(keyBenefit.path("cate").path("idx").asInt() == 28 ||
                                keyBenefit.path("cate").path("name").asText("").equals("유의사항")
                ) {
                    data.gradeDescription = keyBenefit.path("info").asText("");
                    continue;
                }
                
                Benefit benefit = new Benefit();
                benefit.title = keyBenefit.path("title").asText("");
                benefit.summary = keyBenefit.path("comment").asText("");
                benefit.description = keyBenefit.path("info").asText("");

                data.benefits.add(benefit);
            }
        }

        return data;
    }

    private static String MatchCardType(String cate) throws Exception {
        switch (cate) {
            case "CRD": return "CREDIT";
            case "CHK": return "CHECK";
            default: return "";
        }
    }
}
