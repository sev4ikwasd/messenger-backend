package ru.miit.messenger_backend.config.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(@NotNull @NotEmpty String username, @NotNull @NotEmpty String password) {
}
