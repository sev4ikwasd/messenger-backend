package ru.miit.messenger_backend.adapter.in;

import lombok.RequiredArgsConstructor;
import org.jmolecules.architecture.hexagonal.PrimaryAdapter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import ru.miit.messenger_backend.application.domain.dto.SendMessageDto;
import ru.miit.messenger_backend.application.port.in.ManageMessage;

import java.security.Principal;

@Controller
@PrimaryAdapter
@Validated
@RequiredArgsConstructor
public class ManageMessageWebSocket {
    private final ManageMessage manageMessage;

    @MessageMapping("/send")
    public void send(@Payload SendMessageDto message,
                     Principal user) {
        manageMessage.sendMessage(user.getName(), message);
    }
}
