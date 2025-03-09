package ru.miit.messenger_backend.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jmolecules.architecture.hexagonal.PrimaryAdapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.miit.messenger_backend.application.domain.dto.*;
import ru.miit.messenger_backend.application.port.in.ManageUser;

@RestController
@PrimaryAdapter
@RequestMapping("/user")
@Validated
@Tag(name = "User management")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ManageUserHttp {
    private final ManageUser manageUser;

    @PostMapping("/register")
    @Operation(summary = "Register user")
    ResponseEntity<?> registerUser(@AuthenticationPrincipal UserDetails user, @RequestBody RegisterUserDto registerUserDto) {
        manageUser.registerUser(user.getUsername(), registerUserDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/replenish")
    @Operation(summary = "Replenish user keys")
    ResponseEntity<?> addKeys(@AuthenticationPrincipal UserDetails user, @RequestBody AddKeysDto addKeysDto) {
        manageUser.addKeys(user.getUsername(), addKeysDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get company users info")
    ResponseEntity<Page<UserInfoDto>> getUsersInfo(@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Pagination page number") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Size of page") Integer size) {
        return ResponseEntity.ok(manageUser.getUsersInfo(PageRequest.of(page, size)));
    }

    @GetMapping("/bundle/{uid}")
    @Operation(summary = "Get key bundle for user")
    ResponseEntity<UserBundleDto> getUserBundle(@PathVariable(value = "uid") @Parameter(description = "Uid of user to get bundle of") String uid) {
        return ResponseEntity.ok(manageUser.getUserBundle(uid));
    }

    @GetMapping("/keys")
    @Operation(summary = "Get key status for user")
    ResponseEntity<OneTimeKeysStatus> getOneTimeKeysStatus(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageUser.getOneTimeKeysStatus(user.getUsername()));
    }
}
