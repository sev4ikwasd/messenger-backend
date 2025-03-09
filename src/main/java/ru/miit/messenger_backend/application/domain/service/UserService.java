package ru.miit.messenger_backend.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.jmolecules.architecture.hexagonal.Application;
import org.jmolecules.ddd.annotation.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.miit.messenger_backend.application.domain.dto.*;
import ru.miit.messenger_backend.application.domain.model.LdapUser;
import ru.miit.messenger_backend.application.domain.model.User;
import ru.miit.messenger_backend.application.domain.model.UserOneTimeKey;
import ru.miit.messenger_backend.application.port.in.ManageUser;
import ru.miit.messenger_backend.application.port.out.LdapRepository;
import ru.miit.messenger_backend.application.port.out.UserRepository;
import ru.miit.messenger_backend.exception.BusinessRuleViolationException;
import ru.miit.messenger_backend.exception.ResourceNotFoundException;
import ru.miit.messenger_backend.utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Application
@Service
@Transactional
public class UserService implements ManageUser {
    private final UserRepository userRepository;
    private final LdapRepository ldapRepository;

    private User getUser(String uid) {
        if (ldapRepository.getUserByUid(uid).isEmpty()) throw new ResourceNotFoundException("User not found");
        Optional<User> userOptional = userRepository.getUserByUid(uid);
        if (userOptional.isEmpty()) throw new ResourceNotFoundException("User not registered");
        return userOptional.get();
    }

    @Override
    public void registerUser(String uid, RegisterUserDto registerUserDto) {
        if (ldapRepository.getUserByUid(uid).isEmpty()) throw new ResourceNotFoundException("User not found");
        if (userRepository.getUserByUid(uid).isPresent())
            throw new BusinessRuleViolationException("User already exists");
        User user = new User(uid, LocalDateTime.now(), registerUserDto.identityPublicKey(), registerUserDto.signedPublicKey(),
                registerUserDto.oneTimeKeyList().stream()
                        .map(key -> new UserOneTimeKey(key.oneTimeKey(), key.number()))
                        .collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Override
    public void addKeys(String uid, AddKeysDto addKeysDto) {
        User user = getUser(uid);
        user.replenishOneTimeKeys(addKeysDto.oneTimeKeyList().stream()
                .map(key -> new UserOneTimeKey(key.oneTimeKey(), key.number()))
                .collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Override
    public Page<UserInfoDto> getUsersInfo(Pageable pageable) {
        List<LdapUser> ldapUserList = ldapRepository.getAllUsers();

        List<UserInfoDto> userInfoDtoList = new ArrayList<>();
        for (LdapUser info : ldapUserList) {
            boolean registered = userRepository.getUserByUid(info.getUid()).isPresent();
            userInfoDtoList.add(new UserInfoDto(info.getUid(), info.getName(), registered));
        }

        return Utils.paginate(userInfoDtoList, pageable);
    }

    @Override
    public UserBundleDto getUserBundle(String uid) {
        User user = getUser(uid);
        Optional<UserOneTimeKey> key = user.takeOneTimeKey();
        userRepository.save(user);

        Optional<byte[]> keyValue = Optional.empty();
        if (key.isPresent())
            keyValue = Optional.of(key.get().getPublicOneTimeKey());

        return new UserBundleDto(user.getIdentityPublicKey(), user.getSignedPublicKey(), keyValue);
    }

    @Override
    public OneTimeKeysStatus getOneTimeKeysStatus(String uid) {
        User user = getUser(uid);

        return new OneTimeKeysStatus(user.isRequiresReplenishingKeys(),
                user.getUserOneTimeKeys().stream()
                        .map(UserOneTimeKey::getKeyNumber)
                        .collect(Collectors.toList()));
    }
}
