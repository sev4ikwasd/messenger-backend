package ru.miit.messenger_backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.miit.messenger_backend.domain.User;
import ru.miit.messenger_backend.dto.request.RegisterUser;
import ru.miit.messenger_backend.dto.request.VaultUpdate;
import ru.miit.messenger_backend.dto.response.PreKeyBundle;
import ru.miit.messenger_backend.dto.response.UserInfo;
import ru.miit.messenger_backend.dto.response.UserVault;
import ru.miit.messenger_backend.repository.UserRepository;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{uid}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable String uid) {
        return ResponseEntity.ok(userRepository.getUserInfo(uid));
    }

    @GetMapping("/{uid}/bundle")
    public ResponseEntity<PreKeyBundle> getPreKeyBundle(@PathVariable String uid) {
        return ResponseEntity.ok(userRepository.getPreKeyBundle(uid));
    }

    @GetMapping("/vault")
    public ResponseEntity<UserVault> getUserVault(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userRepository.getUserVault(user.getUid()));
    }

    //TODO queued messages

    //TODO renew one time keys

    @PostMapping("/vault")
    public ResponseEntity<?> updateUserVault(@AuthenticationPrincipal User user, @RequestBody VaultUpdate vault) {
        userRepository.updateUserVault(user.getUid(), vault.vault());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@AuthenticationPrincipal User user, @RequestBody RegisterUser registerUser) {
        userRepository.registerNewUser(user.getUid(), registerUser);
        return ResponseEntity.ok().build();
    }
}
