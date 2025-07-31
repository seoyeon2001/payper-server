package com.payper.external.codef.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    String code; // 필수
    String extraMessage; // 선택
    String message; // 필수
    String transactionId; // 필수
}
