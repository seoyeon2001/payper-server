package com.payper.external.codef.dto.output;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessItem {
    private String clientType;
    private String code;
    private String loginType;
    private String countryCode;
    private String organization;
    private String extraMessage;
    private String businessType;
    private String message;
    private String transactionId;
}
