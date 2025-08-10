package com.payper.global.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private String partnerName;
    private String cardName;
    private String cardSummary;
}
