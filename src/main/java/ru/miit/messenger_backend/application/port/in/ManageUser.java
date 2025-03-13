package ru.miit.messenger_backend.application.port.in;

import org.jmolecules.architecture.hexagonal.PrimaryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.miit.messenger_backend.application.domain.dto.*;

@PrimaryPort
public interface ManageUser {
    void registerUser(String uid, RegisterUserDto registerUserDto);

    void updateUserData(String uid, UpdateUserDataDto updateUserDataDto);

    UserDataDto getUserData(String uid);

    void addKeys(String uid, AddKeysDto addKeysDto);

    UserInfoDto getUserInfo(String uid);

    Page<UserInfoDto> getUsersInfo(Pageable pageable);

    UserBundleDto getUserBundle(String uid);

    OneTimeKeysStatusDto getOneTimeKeysStatus(String uid);
}
