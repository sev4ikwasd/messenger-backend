package ru.miit.messenger_backend.application.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OneTimeKeyDto(@NotNull UUID number, @NotEmpty byte[] oneTimeKey) {
}
