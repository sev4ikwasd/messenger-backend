package ru.miit.messenger_backend.application.port.in;

import org.jmolecules.architecture.hexagonal.PrimaryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.miit.messenger_backend.application.domain.dto.*;

@PrimaryPort
public interface ManageUser {
    void registerUser(String uid, RegisterUserDto registerUserDto);

    void addKeys(String uid, AddKeysDto addKeysDto);

    Page<UserInfoDto> getUsersInfo(Pageable pageable);

    UserBundleDto getUserBundle(String uid);

    OneTimeKeysStatus getOneTimeKeysStatus(String uid);
}
