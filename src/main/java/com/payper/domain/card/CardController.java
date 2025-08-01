package com.payper.domain.card;

import com.payper.domain.card.dto.CardResponse;
import com.payper.domain.card.dto.RegisterCardMeRequest;
import com.payper.domain.user.UserService;
import com.payper.global.security.domain.CustomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
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
        log.info("시중카드 조회 - cardId : {}", cardId);
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

    @DeleteMapping("/me/{cardId}")
    public ResponseEntity<Void> deleteCardMe(@AuthenticationPrincipal CustomUser customUser, @PathVariable Integer cardId) {
        Integer userId = userService.getUserId(customUser);
        log.info("내 카드 삭제 - userId : {}, cardId : {} ", userId, cardId);

        cardService.deleteCardMe(userId, cardId);
        return ResponseEntity.ok().build();
    }
}
