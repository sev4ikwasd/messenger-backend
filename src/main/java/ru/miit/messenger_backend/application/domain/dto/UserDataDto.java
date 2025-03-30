package ru.miit.messenger_backend.application.domain.dto;

public record UserDataDto(byte[] userData, byte[] protectedSymmetricKey) {
}
