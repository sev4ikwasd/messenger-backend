package ru.miit.messenger_backend.application.domain.dto;

import java.util.List;
import java.util.UUID;

public record OneTimeKeysStatus(boolean requiresReplenishing, List<UUID> currentKeys) {
}
