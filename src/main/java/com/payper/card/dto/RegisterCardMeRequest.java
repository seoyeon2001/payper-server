package com.payper.card.dto;

import lombok.Data;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Data
public class RegisterCardMeRequest {
    private Integer cardId;
}