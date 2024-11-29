package ru.miit.messenger_backend.repository;

import ru.miit.messenger_backend.entity.UserPrivateKeyEntity;

import java.util.List;

public interface UserPrivateKeyRepository {
    void createUserPrivateKeyEntity(final UserPrivateKeyEntity userPrivateKeyEntity);

    UserPrivateKeyEntity getUserPrivateKeyEntity(final int userPrivateKeyId);

    void updateUserPrivateKeyEntity(final UserPrivateKeyEntity userPrivateKeyEntity);

    void deleteUserPrivateKeyEntity(final int userPrivateKeyId);

    List<UserPrivateKeyEntity> getAllUserEntities();

}
