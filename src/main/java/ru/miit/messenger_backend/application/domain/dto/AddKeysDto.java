package ru.miit.messenger_backend.application.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddKeysDto(@NotNull @NotEmpty List<OneTimeKeyDto> oneTimeKeyList) {
}
