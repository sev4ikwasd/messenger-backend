package ru.miit.messenger_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.miit.messenger_backend.domain.User;
import ru.miit.messenger_backend.dto.request.VaultUpdate;
import ru.miit.messenger_backend.dto.response.PreKeyBundle;
import ru.miit.messenger_backend.dto.response.UserInfo;
import ru.miit.messenger_backend.dto.response.UserVault;
import ru.miit.messenger_backend.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{uid}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable String uid) {
        //TODO
        return null;
    }

    @GetMapping("/{uid}/bundle")
    public ResponseEntity<PreKeyBundle> getPreKeyBundle(@PathVariable String uid) {
        return ResponseEntity.ok(userRepository.getPreKeyBundle(userRepository.getIdByUid(uid)));
    }

    @GetMapping("/vault")
    public ResponseEntity<UserVault> getUserVault(@AuthenticationPrincipal User user) {
        //TODO id
        return ResponseEntity.ok(userRepository.getUserVault(userRepository.getIdByUid(user.getUid())));
    }

    //TODO queued messages

    //TODO renew one time keys

    @PostMapping("/create")
    public ResponseEntity<?> createNewUser(@AuthenticationPrincipal User user, @RequestBody VaultUpdate vault) {
        //TODO id
        userRepository.updateUserVault(userRepository.getIdByUid(user.getUid()), vault.vault());
        return ResponseEntity.ok().build();
    }
}
