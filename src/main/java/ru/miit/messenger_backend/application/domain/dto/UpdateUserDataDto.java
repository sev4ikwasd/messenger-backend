package ru.miit.messenger_backend.application.domain.dto;

import jakarta.validation.constraints.NotEmpty;

public record UpdateUserDataDto(@NotEmpty byte[] userData) {
}
