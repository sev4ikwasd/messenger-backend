package ru.miit.messenger_backend.config.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.miit.messenger_backend.dto.response.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<AuthResponse> authenticateRequest(Authentication authentication) {
        final String token = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}