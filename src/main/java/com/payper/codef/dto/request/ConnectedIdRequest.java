package com.payper.codef.dto.request;

import com.payper.codef.util.OrganizationCode;

public record ConnectedIdRequest (
        String companyId, // 실제 카드사의 아이디
        String companyPassword, // 실제 카드사의 비밀번호
        OrganizationCode organizationCode // 카드사 코드
) {}