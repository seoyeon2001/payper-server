package com.payper.external.codef.util;

import lombok.Getter;

@Getter
public enum OrganizationCode { // 기관 코드 아이디
    KB국민카드("0301"),
    현대카드("0302"),
    삼성카드("0303"),
    NH농협카드("0304"),
    BC카드("0305"),
    신한카드("0306"),
    씨티카드("0307"),
    우리카드("0309"),
    롯데카드("0311"),
    하나카드("0313"),
    전북은행("0315"),
    광주은행("0316"),
    SH수협은행("0320"),
    제주은행("0321"),
    BC바로카드("0001"), // 임시 생성
    IBK기업은행("0002"); // 임시 생성

    private final String code;

    OrganizationCode(String code) {
        this.code = code;
    }
}

