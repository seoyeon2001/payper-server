package com.payper.domain.fcmToken.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmToken {
    private Integer userId;
    private String token;
    private FcmTokenStatus status;
    private Date lastModifiedAt;
}
