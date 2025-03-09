package ru.miit.messenger_backend.application.domain.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendMessageDto(@NotNull @NotEmpty String receiverUid, @NotNull UUID messageNumber,
                             @NotNull @NotEmpty byte[] message, @Nullable String groupOu) {
}
