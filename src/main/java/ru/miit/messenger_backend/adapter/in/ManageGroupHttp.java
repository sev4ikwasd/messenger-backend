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
import ru.miit.messenger_backend.application.domain.dto.GroupDto;
import ru.miit.messenger_backend.application.domain.dto.UserInfoDto;
import ru.miit.messenger_backend.application.port.in.ManageGroup;

@RestController
@PrimaryAdapter
@RequestMapping("/group")
@Validated
@Tag(name = "Group management")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ManageGroupHttp {
    private final ManageGroup manageGroup;

    @GetMapping
    @Operation(summary = "Get groups avaliable to user")
    ResponseEntity<Page<GroupDto>> getAvailableGroups(@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Pagination page number") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Size of page") Integer size,
                                                      @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageGroup.getAvailableGroups(user.getUsername(), PageRequest.of(page, size)));
    }

    @GetMapping("/{ou}")
    @Operation(summary = "Get users info of group")
    ResponseEntity<Page<UserInfoDto>> getGroupUsers(@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Pagination page number") Integer page,
                                                    @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Size of page") Integer size,
                                                    @AuthenticationPrincipal UserDetails user,
                                                    @PathVariable("ou") @Parameter(description = "Group identifier") String ou) {
        return ResponseEntity.ok(manageGroup.getGroupUsers(user.getUsername(), ou, PageRequest.of(page, size)));
    }
}
