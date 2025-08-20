package com.payper.external.codef.dto.request;

import com.payper.external.codef.util.OrganizationCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalListRequest {
    private OrganizationCode organizationName; // 카드사 코드
    private String startDate;
    private String endDate;
    private String memberStoreInfoType; // "0": 미포함, "1": 가맹점 포함, "2": 부가세 포함, "3": 전체 포함
}
