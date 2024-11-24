package ru.miit.messenger_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserDataEntity {
    private int id;
    private int userId;
    private byte[] encryptedData;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
}
