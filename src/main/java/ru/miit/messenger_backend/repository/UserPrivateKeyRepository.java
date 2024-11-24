package ru.miit.messenger_backend.repository;

import ru.miit.messenger_backend.entity.UserPrivateKeyEntity;

import java.util.List;

public interface UserPrivateKeyRepository {
    public void createUserPrivateKeyEntity(final UserPrivateKeyEntity userPrivateKeyEntity);
    public UserPrivateKeyEntity getUserPrivateKeyEntity(final int userPrivateKeyId);
    public void updateUserPrivateKeyEntity(final UserPrivateKeyEntity userPrivateKeyEntity);
    public void deleteUserPrivateKeyEntity(final int userPrivateKeyId);
    public List<UserPrivateKeyEntity> getAllUserEntities();

}
