package ru.miit.messenger_backend.dto.response;

public record PreKeyBundle(byte[] identityPublicKey, byte[] signedPublicKey, Byte[] oneTimePublicKey) {
}
