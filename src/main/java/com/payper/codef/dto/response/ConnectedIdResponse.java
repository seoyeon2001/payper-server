package com.payper.codef.dto.response;

import com.payper.codef.dto.output.ErrorItem;
import com.payper.codef.dto.output.SuccessItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedIdResponse {
    private List<SuccessItem> successList;
    private List<ErrorItem> errorList;
    private String connectedId; // 실패하면 값이 없음
}
