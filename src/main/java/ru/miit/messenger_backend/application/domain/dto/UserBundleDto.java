package ru.miit.messenger_backend.application.domain.dto;

import java.util.Optional;

public record UserBundleDto(byte[] identityPublicKey, byte[] signedPublicKey, Optional<byte[]> oneTimePublicKey) {
}
