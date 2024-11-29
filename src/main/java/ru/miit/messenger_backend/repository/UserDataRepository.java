package ru.miit.messenger_backend.repository;

import ru.miit.messenger_backend.entity.UserDataEntity;

import java.util.List;

public interface UserDataRepository {
    void createUserDataEntity(final UserDataEntity userDataEntity);

    UserDataEntity getUserDataEntity(final int userDataId);

    void updateUserDataEntity(final UserDataEntity userDataEntity);

    void deleteUserDataEntity(final int userDataId);

    List<UserDataEntity> getAllUserDataEntities();
}
