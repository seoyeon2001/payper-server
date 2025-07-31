package com.payper.card.controller;

import com.payper.card.dto.CardResponse;
import com.payper.card.dto.RegisterCardMeRequest;
import com.payper.card.exception.CardNotFoundException;
import com.payper.card.service.CardService;
import com.payper.security.domain.CustomUser;
import com.payper.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
    private final UserService userService;

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

    @GetMapping("/search")
    public ResponseEntity<Map<String, List<CardResponse>>> searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> cardCompany
    ) {
        List<CardResponse> cards = cardService.searchCards(name, type, category, cardCompany);
        log.info("검색 카드 조회");
        return ResponseEntity.ok(Map.of("cards", cards));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, List<CardResponse>>> getAllCardsByMe(@AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);
        log.info("내 카드 리스트 조회 - userId: {}", userId);

        List<CardResponse> cards = cardService.getAllCardsByMe(userId);
        return ResponseEntity.ok(Map.of("cards", cards));
    }

    @PostMapping("/me")
    public ResponseEntity<Void> registerCardMe(@RequestBody RegisterCardMeRequest request, @AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);
        log.info("내 카드로 등록 - userId : {}, cardId : {} ", userId, request.getCardId());

        cardService.registerCardMe(request, userId);
        return ResponseEntity.ok().build();
    }
}
