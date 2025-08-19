package com.payper.domain.card;

import com.payper.domain.card.dto.request.RegisterCardMeRequest;
import com.payper.domain.card.dto.request.RegisterCardRequest;
import com.payper.domain.card.dto.request.UpdateCardRequest;
import com.payper.domain.card.dto.response.CardResponse;
import com.payper.domain.card.dto.response.CardsResponse;
import com.payper.domain.card.service.CardService;
import com.payper.domain.card.service.SearchService;
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

    private final SearchService searchService;

    @GetMapping("")
    public ResponseEntity<Map<String, List<CardResponse>>> getAllCards() {
        log.info("시중카드 전체 리스트 조회");

        List<CardResponse> cards = cardService.getAllCards();
        return ResponseEntity.ok(Map.of("cards", cards));
    }

    @PostMapping("")
    public ResponseEntity<Void> registerCard(@RequestBody RegisterCardRequest request){
        cardService.registerCard(request);

        log.info("카드 등록 - card : {} ", request.getCardName());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable(name = "cardId") Integer cardId,
                                                    @AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);

        log.info("시중카드 조회 - cardId : {}", cardId);
        CardResponse card = cardService.getCardById(cardId, userId);
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Void> updateCard(@RequestBody UpdateCardRequest request, @PathVariable(name = "cardId") Integer cardId){
        cardService.updateCard(request,cardId);

        log.info("카드 갱신 -card : {} ", request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<CardsResponse> searchCards(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> cardCompany,
            @RequestParam(defaultValue = "0") int page //Integer면 변환될 때 null이 될 수 있어서 int로 지정
    ) {
        log.info("검색 카드 조회");

        if(category!=null)
            category.sort(null);

        if(cardCompany!=null)
            cardCompany.sort(null);

        //검색 옵션 값 지정
        SearchOptions searchOptions = SearchOptions.create(
                name,type,category,cardCompany,page
        );

        return ResponseEntity.ok(searchService.searchCardsPhase1(searchOptions));
    }

    @GetMapping("/me")
    public ResponseEntity<CardsResponse> getAllCardsByMe(@AuthenticationPrincipal CustomUser customUser) {
        List<CardResponse> cards = cardService.getAllCardsByMe(customUser.getUser().getUserId());
        return ResponseEntity.ok(new CardsResponse(false, 0, cards));
    }

    @PostMapping("/me")
    public ResponseEntity<Void> registerCardMe(@RequestBody RegisterCardMeRequest request, @AuthenticationPrincipal CustomUser customUser) {
        Integer userId = userService.getUserId(customUser);
        log.info("내 카드로 등록 - userId : {}, cardId : {} ", userId, request.getCardId());

        cardService.registerCardMe(request, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/{cardId}")
    public ResponseEntity<Void> deleteCardMe(@AuthenticationPrincipal CustomUser customUser, @PathVariable(name = "cardId") Integer cardId) {
        Integer userId = userService.getUserId(customUser);
        log.info("내 카드 삭제 - userId : {}, cardId : {} ", userId, cardId);

        cardService.deleteCardMe(userId, cardId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cardId}")
    public ResponseEntity<Void> deactivateCard(@PathVariable(name = "cardId") Integer cardId) {
        cardService.deactivateCard(cardId);

        log.info("카드 만료 - cardId : {} ", cardId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable(name = "cardId") Integer cardId) {
        cardService.deleteCard(cardId);

        log.info("카드 삭제 - cardId : {} ", cardId);
        return ResponseEntity.ok().build();
    }
}
