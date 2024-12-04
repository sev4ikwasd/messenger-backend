package ru.miit.messenger_backend.dto.request;

public record MessageReceive(String userRecipientUid, String message) {
}
