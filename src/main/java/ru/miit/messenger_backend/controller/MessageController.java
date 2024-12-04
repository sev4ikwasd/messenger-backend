package ru.miit.messenger_backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import ru.miit.messenger_backend.domain.User;
import ru.miit.messenger_backend.dto.request.MessageReceive;
import ru.miit.messenger_backend.dto.response.MessageSend;

@Controller
public class MessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/send")
    public void send(@Payload MessageReceive messageReceive, @AuthenticationPrincipal User user) {
        simpMessagingTemplate.convertAndSendToUser(messageReceive.userRecipientUid(), "/queue/reply", new MessageSend(user.getUid(), messageReceive.message()));
    }
}
