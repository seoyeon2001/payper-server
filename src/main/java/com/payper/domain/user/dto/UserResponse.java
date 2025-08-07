package com.payper.domain.user.dto;

import com.payper.domain.user.domain.User;
import com.payper.global.exception.CustomIllegalArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String nickname;

    public static UserResponse toDto(User user) {
        if(user == null) {
            throw new CustomIllegalArgumentException("user");
        }

        return UserResponse.builder()
                .id(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }
}