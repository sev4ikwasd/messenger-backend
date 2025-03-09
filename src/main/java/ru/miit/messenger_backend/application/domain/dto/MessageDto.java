package ru.miit.messenger_backend.application.domain.dto;

import java.util.UUID;

public record MessageDto(UUID num, String sender, byte[] message) {
}
