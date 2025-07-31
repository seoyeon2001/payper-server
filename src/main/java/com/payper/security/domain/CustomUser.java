package com.payper.security.domain;

import com.payper.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Getter
@Setter
public class CustomUser extends org.springframework.security.core.userdetails.User {
    static private final String commonPassword = "abcd";
    private User user;

    public CustomUser(String userId, String password,
                      Collection<? extends GrantedAuthority> authorities) {
        super(userId, password, authorities);
    }

    public CustomUser(User domainUser) {
        super(
                domainUser.getUserId().toString(),
                commonPassword,
                Optional.ofNullable(domainUser.getAuthList()).orElse(Collections.emptyList())
        );

        this.user = domainUser;
    }
}
