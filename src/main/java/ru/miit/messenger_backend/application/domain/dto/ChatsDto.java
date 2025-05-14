package ru.miit.messenger_backend.application.domain.dto;

import java.time.LocalDateTime;

public record ChatsDto(boolean isGroup, String id, String name, String sender, byte[] lastMessage, LocalDateTime time) {
}
