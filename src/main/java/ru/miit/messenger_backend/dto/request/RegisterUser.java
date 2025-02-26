package ru.miit.messenger_backend.dto.request;

import java.util.List;

public record RegisterUser(byte[] masterPasswordHash, byte[] protectedSymmetricKey, byte[] identityPublicKey, byte[] signedPublicKey, List<OneTimeKey> publicOneTimeKeyList) {
}
