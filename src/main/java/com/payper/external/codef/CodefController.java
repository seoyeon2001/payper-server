package com.payper.external.codef;

import com.payper.external.codef.dto.output.CodefStandardResponse;
import com.payper.external.codef.dto.request.MyCardListRequest;
import com.payper.external.codef.dto.request.ConnectedIdRequest;
import com.payper.external.codef.dto.response.MyCardListResponse;
import com.payper.external.codef.dto.response.ConnectedIdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/codef")
public class CodefController {
    private final CodefService codefService;

    // 계정 생성 - connected id 생성
    @PostMapping("/account/create/{userId}")
    public CodefStandardResponse<ConnectedIdResponse> createConnectedId(@RequestBody ConnectedIdRequest request, @PathVariable(name = "userId") Integer userId) {
        return codefService.createConnectedId(request, userId);
    }

    // 보유카드 조회
    @PostMapping("/cards/me")
    public CodefStandardResponse<MyCardListResponse> getMyCardList(@RequestBody MyCardListRequest request, Integer userId) {
        return codefService.getMyCardList(request, userId);
    }
}