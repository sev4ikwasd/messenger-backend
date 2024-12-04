package ru.miit.messenger_backend.repository;

import ru.miit.messenger_backend.dto.response.PreKeyBundle;
import ru.miit.messenger_backend.dto.response.UserVault;

public interface UserRepository {
    int getIdByUid(final String uid);

    //TODO user info

    PreKeyBundle getPreKeyBundle(final int userId);

    UserVault getUserVault(final int userId);

    void updateUserVault(final int userId, byte[] vaultUpdate);
}
