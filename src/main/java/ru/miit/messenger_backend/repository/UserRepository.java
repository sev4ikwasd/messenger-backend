package ru.miit.messenger_backend.repository;

import ru.miit.messenger_backend.entity.UserEntity;

import java.util.List;

public interface UserRepository {
    public void createUserEntity(final UserEntity userEntity);
    public UserEntity getUserEntityById(final int userId);
    public void updateUserEntity(final UserEntity userEntity);
    public void deleteUserEntity(final int userId);
    public List<UserEntity> getAllUserEntities();
}
