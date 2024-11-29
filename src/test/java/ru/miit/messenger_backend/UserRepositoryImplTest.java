package ru.miit.messenger_backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.miit.messenger_backend.entity.UserEntity;
import ru.miit.messenger_backend.repository.impl.UserRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class UserRepositoryImplTest {
    private final int DEFAULT_USER_ID = 2;
    private final LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.of(2024, 11, 11, 11, 11);
    private final byte[] DEFAULT_MASTER_PASSWORD_HASH = "masterPasswordHash".getBytes();
    private final byte[] DEFAULT_PROTECTED_SYMMETRIC_KEY = "protectedSymmetricKey".getBytes();
    private final UserEntity DEFAULT_USER_ENTITY = UserEntity.builder()
            .id(DEFAULT_USER_ID)
            .uid(2)
            .lastVisited(DEFAULT_DATE_TIME)
            .masterPasswordHash(DEFAULT_MASTER_PASSWORD_HASH)
            .protectedSymmetricKey(DEFAULT_PROTECTED_SYMMETRIC_KEY)

            .build();
    @Autowired
    private UserRepositoryImpl userRepository;

    @Test
    public void successCreateUser() {
        UserEntity newUser = UserEntity.builder()
                .uid(2)
                .lastVisited(DEFAULT_DATE_TIME)
                .masterPasswordHash(DEFAULT_MASTER_PASSWORD_HASH)
                .protectedSymmetricKey(DEFAULT_PROTECTED_SYMMETRIC_KEY)

                .build();

        userRepository.createUserEntity(newUser);

    }

    @Test
    public void successGetUserEntityById() {

        UserEntity actualUserEntity = userRepository.getUserEntityById(DEFAULT_USER_ID);

        Assertions.assertEquals(DEFAULT_USER_ENTITY, actualUserEntity);
    }

    @Test
    public void successUpdateUserEntity() {
        UserEntity oldUserEntity = userRepository.getUserEntityById(DEFAULT_USER_ID);

        Assertions.assertEquals(oldUserEntity, DEFAULT_USER_ENTITY);

        UserEntity newUserEntity = UserEntity.builder()
                .id(DEFAULT_USER_ID)
                .uid(3)
                .lastVisited(DEFAULT_DATE_TIME)
                .masterPasswordHash(DEFAULT_MASTER_PASSWORD_HASH)
                .protectedSymmetricKey(DEFAULT_PROTECTED_SYMMETRIC_KEY)

                .build();

        userRepository.updateUserEntity(newUserEntity);

        UserEntity expectedUserEntity = userRepository.getUserEntityById(DEFAULT_USER_ID);

        Assertions.assertEquals(expectedUserEntity, newUserEntity);
    }

    @Test
    public void successDeleteUserEntity() {
        userRepository.deleteUserEntity(DEFAULT_USER_ID);
    }

    @Test
    public void successGetAllUserEntity() {
        List<UserEntity> userEntityList = userRepository.getAllUserEntities();

        Assertions.assertEquals(userEntityList.size(), 4);
    }


}
