package com.payper.codef.dto.request;

import com.payper.codef.util.OrganizationCode;

public record MyCardListRequest(
        OrganizationCode organizationCode, // 카드사 코드
        String inquiryType // 카드 이미지 포함여부 1: 포함, 0: 미포함
) {}