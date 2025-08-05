package com.payper.external.crawling.service.sub;

import com.payper.external.crawling.dto.Grade;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CardGradeCleanService {

    private static final Pattern GRADE_PATTERN = Pattern.compile("전월 이용실적\\s*(\\d+)만원 이상[:：]?\\s*(\\d+)만원?");

    public List<Grade> cleanGrade(String gradeDescription) {
        List<Grade> grades = new ArrayList<>();
        Document doc = Jsoup.parse(gradeDescription);
        Elements tables = doc.select("table");

        boolean tableParsed = false;

        for (Element table : tables) {
            Elements rows = table.select("tr");
            if (rows.size() < 2) continue;

            Element headerRow = rows.get(0);
            Element valueRow = rows.get(1);

            if (!headerRow.text().contains("전월 이용실적 구간")) continue;

            Elements headers = headerRow.select("td");
            Elements values = valueRow.select("td");

            for (Integer i = 1; i < headers.size(); i++) {
                String headerText = headers.get(i).text();
                String valueText = values.get(i).text();

                Long start = parseWonRangeStart(headerText);
                Long discount = parseWon(valueText); // 숫자 없으면 0

                grades.add(Grade.builder()
                        .start(start)
                        .totalDiscount(discount)
                        .build());
            }

            tableParsed = true;
            break;
        }

        // fallback: 문장 기반 파싱
        if (!tableParsed) {
            String text = doc.text();
            Matcher matcher = GRADE_PATTERN.matcher(text);

            while (matcher.find()) {
                long start = Long.parseLong(matcher.group(1)) * 10000;
                long discount = Long.parseLong(matcher.group(2)) * 10000;
                grades.add(Grade.builder()
                        .start(start)
                        .totalDiscount(discount)
                        .build());
            }
        }

        return grades;
    }

    private static Long parseWon(String text) {
        try {
            return Long.parseLong(text.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0L;
        }
    }

    private static Long parseWonRangeStart(String text) {
        if (text.contains("미만")) return 0L;
        try {
            return Long.parseLong(text.replaceAll("[^0-9]", "")) * 10000;
        } catch (Exception e) {
            return 0L;
        }
    }
}
