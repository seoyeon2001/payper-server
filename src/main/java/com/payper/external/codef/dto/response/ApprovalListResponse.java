package com.payper.external.codef.dto.response;

import com.payper.external.codef.dto.output.ApprovalInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalListResponse {
    private List<ApprovalInfo> approvalList;
}