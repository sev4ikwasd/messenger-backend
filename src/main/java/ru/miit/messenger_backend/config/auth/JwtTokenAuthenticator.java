package ru.miit.messenger_backend.config.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenAuthenticator {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtTokenAuthenticator(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public UsernamePasswordAuthenticationToken authenticate(String token) {
        String username = jwtUtil.extractUserName(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.isTokenValid(token, userDetails)) {
            throw new AuthenticationException("") {
                @Override
                public String getMessage() {
                    return "Error occurred while authenticating token: " + token;
                }
            };
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
