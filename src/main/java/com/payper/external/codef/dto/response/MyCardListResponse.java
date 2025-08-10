package com.payper.external.codef.dto.response;

import com.payper.external.codef.dto.CardRegistrationResult;
import com.payper.external.codef.dto.output.CardInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyCardListResponse {
    private List<CardInfo> myCardList;
    private List<CardRegistrationResult> registrationResults; // 등록 결과 추가
    private String summary; // 전체 요약 메시지
}