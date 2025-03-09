package ru.miit.messenger_backend.config.auth;

import lombok.AllArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenAuthenticator jwtTokenAuthenticator;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) throw new RuntimeException("No auth header");
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader == null) throw new RuntimeException("No auth header");
            String token = authorizationHeader.split(" ")[1].trim();

            UsernamePasswordAuthenticationToken authentication = jwtTokenAuthenticator.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            accessor.setUser(authentication);
        }
        return message;
    }
}
