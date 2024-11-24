package ru.miit.messenger_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserPrivateKeyEntity {
    private int id;
    private int userId;
    private byte[] encryptedKey;
}
