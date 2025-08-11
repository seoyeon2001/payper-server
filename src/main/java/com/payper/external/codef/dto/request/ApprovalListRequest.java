package com.payper.external.codef.dto.request;

import com.payper.external.codef.util.OrganizationCode;

public record ApprovalListRequest(
        OrganizationCode organizationName, // 카드사 코드
        String startDate,
        String endDate,
        String memberStoreInfoType // 가맹점 정보 -  "0": 미포함, "1": 가맹점 포함, "2":부가세 포함, "3":전체 (가맹점 +부가세) 포함 / default: "0"
) {}