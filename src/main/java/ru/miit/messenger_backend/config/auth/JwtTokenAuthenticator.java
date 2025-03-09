package ru.miit.messenger_backend.config.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtTokenAuthenticator {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

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
