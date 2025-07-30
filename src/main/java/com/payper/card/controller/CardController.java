package com.payper.card.controller;

import com.payper.card.dto.CardResponse;
import com.payper.card.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping("")
    public ResponseEntity<Map<String, List<CardResponse>>> getAllCards() {
        List<CardResponse> cards = cardService.getAllCards();
        log.info("시중카드 전체 리스트 조회");
        return ResponseEntity.ok(Map.of("cards", cards));
    }
}
