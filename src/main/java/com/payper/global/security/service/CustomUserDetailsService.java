package com.payper.global.security.service;

import com.payper.global.security.domain.CustomUser;
import com.payper.global.security.mapper.UserDetailsMapper;
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
    private final UserDetailsMapper userDetailsMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user=userDetailsMapper.get(userId);

        if(user==null){
            throw new UsernameNotFoundException(userId);
        }

        return new CustomUser(user);
    }
}
