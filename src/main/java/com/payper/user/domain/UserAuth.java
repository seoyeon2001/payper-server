package com.payper.user.domain;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
public class UserAuth implements GrantedAuthority {
    private String userId;
    private String auth;

    @Override
    public String getAuthority() {
        return auth;
    }
}
