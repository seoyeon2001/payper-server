package com.payper.external.codef.dto.request;

import com.payper.external.codef.util.OrganizationCode;

public record CreateAccountRequest(
        String companyId, // 실제 카드사의 아이디
        String companyPassword, // 실제 카드사의 비밀번호
        OrganizationCode organizationName // 카드사 코드
) {}