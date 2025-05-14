package ru.miit.messenger_backend.application.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
public class Chat {
    int idSender;
    int idReceiver;
    byte[] message;
    String ouGroup;
    LocalDateTime timeSent;
}
