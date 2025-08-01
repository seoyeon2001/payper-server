package com.payper.global.security.service;

import com.payper.domain.user.UserMapper;
import com.payper.global.security.domain.CustomUser;
import com.payper.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user= userMapper.get(Integer.parseInt(userId));

        if(user==null){
            throw new UsernameNotFoundException(userId);
        }

        return new CustomUser(user);
    }
}
