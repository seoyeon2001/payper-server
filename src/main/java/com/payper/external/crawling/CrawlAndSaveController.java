package com.payper.external.crawling;

import com.payper.external.crawling.dto.CardData;
import com.payper.external.crawling.service.CardSaveService;
import com.payper.external.crawling.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
@Slf4j
public class CrawlAndSaveController {
    private final CrawlingService crawlingService;
    private final CardSaveService cardSaveService;

    @GetMapping("")
    public ResponseEntity<Void> runCrawlingAndSave(@RequestParam("start") int start,
                                                   @RequestParam("end") int end) {
        int triedCount = end - start + 1, completeCount = 0;
        for(int id = start; id <= end; id++) {
            CardData cardData = crawlingService.crawlsCards(id);

            if(cardData == null) continue;
            cardSaveService.saveFullCardData(cardData);
            completeCount ++;
        }

        log.info("카드 크롤링 및 저장 {}건 중, {}건 완료되었습니다.", triedCount, completeCount);
        return ResponseEntity.ok().build();
    }
}
