package com.payper.global.security.domain;

import com.payper.domain.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Getter
@Setter
public class CustomUser extends org.springframework.security.core.userdetails.User {
    private User user;

    public CustomUser(String userId, String password,
                      Collection<? extends GrantedAuthority> authorities) {
        super(userId, password, authorities);
    }

    public CustomUser(User domainUser) {
        super(
                domainUser.getUserId().toString(),
                domainUser.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority(domainUser.getRole().name())
                )
        );

        this.user = domainUser;
    }
}
