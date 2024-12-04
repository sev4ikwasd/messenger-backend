package ru.miit.messenger_backend.domain;

import lombok.Data;

@Data
public class Message {
    private String message;
    private String recipientUid;
}
