package com.payper.card.controller;

import com.payper.card.dto.CardResponse;
import com.payper.card.exception.CardNotFoundException;
import com.payper.card.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        log.info("시중카드 전체 리스트 조회");

        List<CardResponse> cards = cardService.getAllCards();
        return ResponseEntity.ok(Map.of("cards", cards));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable int cardId) {
        log.info("시중카드 조회");

        if(!cardService.existsById(cardId)) throw new CardNotFoundException();
        CardResponse card = cardService.getCardById(cardId);
        return ResponseEntity.ok(card);
    }
}
