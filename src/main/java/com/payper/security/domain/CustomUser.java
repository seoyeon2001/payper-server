package com.payper.security.domain;

import com.payper.user.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

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
        super(domainUser.getUserId().toString(), commonPassword, domainUser.getAuthList());
        this.user = domainUser;
    }
}
