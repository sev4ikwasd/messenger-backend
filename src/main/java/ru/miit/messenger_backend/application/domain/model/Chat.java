package ru.miit.messenger_backend.application.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Chat {
    int idUser;
    byte[] message;
    String ouGroup;
}
