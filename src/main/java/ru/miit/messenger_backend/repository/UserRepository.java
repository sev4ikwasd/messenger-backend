package ru.miit.messenger_backend.repository;

import ru.miit.messenger_backend.entity.UserEntity;

import java.util.List;

public interface UserRepository {
    void createUserEntity(final UserEntity userEntity);

    UserEntity getUserEntityById(final int userId);

    void updateUserEntity(final UserEntity userEntity);

    void deleteUserEntity(final int userId);

    List<UserEntity> getAllUserEntities();
}
