package com.payper.external.codef.util;

import lombok.Getter;

@Getter
public enum OrganizationCode { // 기관 코드 아이디
    KB("0301"),
    HYUNDAI("0302"),
    SAMSUNG("0303"),
    NH("0304"),
    BC("0305"),
    SHINHAN("0306"),
    CITI("0307"),
    WOORI("0309"),
    LOTTE("0311"),
    HANA("0313"),
    JEONBUK("0315"),
    GWANGJU("0316"),
    SUHYUP("0320"),
    JEJU("0321");

    private final String code;

    OrganizationCode(String code) {
        this.code = code;
    }
}

