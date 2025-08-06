package com.payper.domain.user.dto;

import com.payper.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    private Integer userId; // 사용자 id
    private String oauthProvider; // 소셜 로그인 제공사
    private String oauthId; // 소셜 id
    private String nickname; // 사용자 이름

    static public CreateUserRequest toDTO(String oauthProvider, String oauthId, String nickname) {
        return CreateUserRequest.builder()
                .oauthProvider(oauthProvider)
                .oauthId(oauthId)
                .nickname(nickname)
                .build();
    }
}
