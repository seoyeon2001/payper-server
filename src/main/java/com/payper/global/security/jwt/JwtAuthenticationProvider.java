package com.payper.global.security.jwt;

import com.payper.global.security.exception.JwtAuthenticationException;
import com.payper.global.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final JwtProcessor jwtProcessor;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        try {
            String userId = jwtProcessor.getUserIdString(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            return JwtAuthenticationToken.authenticated(userDetails, token, userDetails.getAuthorities());
        } catch (Exception e) {
            throw new JwtAuthenticationException(e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
