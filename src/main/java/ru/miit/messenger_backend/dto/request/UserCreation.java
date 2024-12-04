package ru.miit.messenger_backend.dto.request;

import java.util.List;

public record UserCreation(byte[] masterPasswordHash, byte[] protectedSymmetricKey, byte[] identityPublicKey,
                           byte[] signedPublicKey, byte[] vault, List<byte[]> oneTimeKeyList) {
}
