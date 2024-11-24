package ru.miit.messenger_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserEntity {
    private int id;
    private int uid;
    private LocalDateTime lastVisited;
    private byte[] masterPasswordHash;
    private byte[] protectedSymmetricKey;

    public UserEntity(int uid, LocalDateTime lastVisited, byte[] masterPasswordHash, byte[] protectedSymmetricKey) {
        this.uid = uid;
        this.lastVisited = lastVisited;
        this.masterPasswordHash = masterPasswordHash;
        this.protectedSymmetricKey = protectedSymmetricKey;
    }
}
