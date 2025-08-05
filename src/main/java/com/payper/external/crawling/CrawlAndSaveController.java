package com.payper.external.crawling;

import com.payper.external.crawling.dto.CardData;
import com.payper.external.crawling.service.CardSaveService;
import com.payper.external.crawling.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
@Slf4j
public class CrawlAndSaveController {
    private final CrawlingService crawlingService;
    private final CardSaveService cardSaveService;

    @GetMapping("")
    public ResponseEntity<Void> runCrawlingAndSave() {
        List<CardData> cardDataList = crawlingService.crawlsCards();

        for (CardData cardData : cardDataList) {
            cardSaveService.saveFullCardData(cardData);
        }

        log.info("크롤링 및 저장 완료: " + cardDataList.size() + "건");
        return ResponseEntity.ok().build();
    }
}
