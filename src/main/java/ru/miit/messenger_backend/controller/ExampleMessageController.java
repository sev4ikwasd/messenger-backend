package ru.miit.messenger_backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.miit.messenger_backend.dto.response.Message;

@Controller
public class ExampleMessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ExampleMessageController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/test")
    public void send(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.userUid(), "/queue/reply", message);
    }
}
