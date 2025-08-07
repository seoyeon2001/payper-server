package com.payper.domain.user.domain;

import com.payper.global.security.util.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Integer userId; // 사용자 id
    private String oauthProvider; // 소셜 로그인 제공사
    private String oauthId; // 소셜 id
    private String nickname; // 사용자 이름
    private String connectedId; // codef connected id

    private List<UserCard> userCardList; // 사용자가 소유한 카드 목록 리스트

    private UserRole role;
    private List<UserAuth> authList;//없어질 예정

    private Boolean isDeleted;
    private Date createdAt;
    private Date deletedAt;
    private Date lastModifiedAt;

    public static User createKakaoUser(String kakaoId, String nickname) {
        return User.builder()
                .oauthProvider(OAuthProvider.KAKAO.name())
                .oauthId(kakaoId)
                .nickname(nickname)
                .role(UserRole.ROLE_USER)
                .build();
    }
}