package ru.miit.messenger_backend.dto.request;

public record OneTimeKey(int keyNumber, byte[] key) {
}
