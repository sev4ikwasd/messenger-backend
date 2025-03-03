package ru.miit.messenger_backend.repository;

import ru.miit.messenger_backend.dto.request.RegisterUser;
import ru.miit.messenger_backend.dto.response.PreKeyBundle;
import ru.miit.messenger_backend.dto.response.UserInfo;
import ru.miit.messenger_backend.dto.response.UserVault;

public interface UserRepository {
    int getIdByUid(final String uid);

    UserInfo getUserInfo(final String uid);

    PreKeyBundle getPreKeyBundle(final String uid);

    UserVault getUserVault(final String uid);

    void updateUserVault(final String uid, final byte[] vaultUpdate);

    void registerNewUser(final String uid, final RegisterUser data);
}
