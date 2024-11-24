package ru.miit.messenger_backend.repository;

import ru.miit.messenger_backend.entity.UserDataEntity;

import java.util.List;

public interface UserDataRepository {
    public void createUserDataEntity(final UserDataEntity userDataEntity);
    public UserDataEntity getUserDataEntity(final int userDataId);
    public void updateUserDataEntity(final UserDataEntity userDataEntity);
    public void deleteUserDataEntity(final int userDataId);
    public List<UserDataEntity> getAllUserDataEntities();
}
