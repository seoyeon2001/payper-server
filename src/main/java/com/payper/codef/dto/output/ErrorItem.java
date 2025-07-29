package com.payper.codef.dto.output;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorItem {
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
