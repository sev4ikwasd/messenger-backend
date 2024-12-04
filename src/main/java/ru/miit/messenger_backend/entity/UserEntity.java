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
    private byte[] identityPublicKey;
    private byte[] signedPublicKey;

    public UserEntity(int uid, LocalDateTime lastVisited, byte[] masterPasswordHash, byte[] protectedSymmetricKey, byte[] identityPublicKey, byte[] signedPublicKey) {
        this.uid = uid;
        this.lastVisited = lastVisited;
        this.masterPasswordHash = masterPasswordHash;
        this.protectedSymmetricKey = protectedSymmetricKey;
        this.identityPublicKey = identityPublicKey;
        this.signedPublicKey = signedPublicKey;
    }
}
