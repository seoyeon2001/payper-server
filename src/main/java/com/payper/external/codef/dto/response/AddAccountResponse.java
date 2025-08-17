package com.payper.external.codef.dto.response;

import com.payper.external.codef.dto.output.ErrorItem;
import com.payper.external.codef.dto.output.SuccessItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAccountResponse {
    private List<SuccessItem> successList;
    private List<ErrorItem> errorList;
    private String connectedId; // 실패하면 값이 없음
}
