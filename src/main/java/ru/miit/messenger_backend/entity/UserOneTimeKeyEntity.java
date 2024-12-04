package ru.miit.messenger_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserOneTimeKeyEntity {
    private int id;
    private int userId;
    private byte[] publicOneTimeKey;
    private int keyNumber;
    private boolean isUsed;

    public UserOneTimeKeyEntity(int userId, byte[] publicOneTimeKey, int keyNumber, boolean isUsed) {
        this.userId = userId;
        this.publicOneTimeKey = publicOneTimeKey;
        this.keyNumber = keyNumber;
        this.isUsed = isUsed;
    }
}
