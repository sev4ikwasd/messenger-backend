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
import ru.miit.messenger_backend.application.domain.dto.ChatsDto;
import ru.miit.messenger_backend.application.domain.dto.MessageDto;
import ru.miit.messenger_backend.application.port.in.ManageMessage;

import java.util.UUID;

@RestController
@PrimaryAdapter
@RequestMapping("/message")
@Validated
@Tag(name = "Messages management")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ManageMessageHttp {
    private final ManageMessage manageMessage;

    @GetMapping("/chats")
    @Operation(summary = "Get chats of user")
    ResponseEntity<Page<ChatsDto>> getChats(@RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Pagination page number") Integer page,
                                            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Size of page") Integer size,
                                            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageMessage.getChats(user.getUsername(), PageRequest.of(page, size)));
    }

    @GetMapping("/user/{uid}/old/")
    @Operation(summary = "Get old messages of user")
    ResponseEntity<Page<MessageDto>> getOldUserMessages(@PathVariable(value = "uid") @Parameter(description = "Uid of other user") String uid,
                                                        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Pagination page number") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Size of page") Integer size,
                                                        @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageMessage.getOldUserMessages(user.getUsername(), uid, PageRequest.of(page, size)));
    }

    @GetMapping("/group/{ou}/old/")
    @Operation(summary = "Get old messages of group")
    ResponseEntity<Page<MessageDto>> getOldGroupMessages(@PathVariable(value = "ou") @Parameter(description = "ou of group") String ou,
                                                         @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Pagination page number") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Size of page") Integer size,
                                                         @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageMessage.getOldGroupMessages(user.getUsername(), ou, PageRequest.of(page, size)));
    }

    @GetMapping("/user/{uid}/new/")
    @Operation(summary = "Get new messages of user")
    ResponseEntity<Page<MessageDto>> getNewUserMessages(@PathVariable(value = "uid") @Parameter(description = "Uid of other user") String uid,
                                                        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Pagination page number") Integer page,
                                                        @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Size of page") Integer size,
                                                        @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageMessage.getNewUserMessages(user.getUsername(), uid, PageRequest.of(page, size)));
    }

    @GetMapping("/group/{ou}/new/")
    @Operation(summary = "Get new messages of group")
    ResponseEntity<Page<MessageDto>> getNewGroupMessages(@PathVariable(value = "ou") @Parameter(description = "ou of group") String ou,
                                                         @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Pagination page number") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Size of page") Integer size,
                                                         @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageMessage.getNewGroupMessages(user.getUsername(), ou, PageRequest.of(page, size)));
    }

    @GetMapping("/user/{uid}/new/count")
    @Operation(summary = "Get new messages of user count")
    ResponseEntity<Integer> newUserMessagesCount(@PathVariable(value = "uid") @Parameter(description = "Uid of other user") String uid,
                                                 @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageMessage.newUserMessagesCount(user.getUsername(), uid));
    }

    @GetMapping("/group/{ou}/new/count")
    @Operation(summary = "Get new messages of group count")
    ResponseEntity<Integer> newGroupMessagesCount(@PathVariable(value = "ou") @Parameter(description = "ou of group") String ou,
                                                  @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(manageMessage.newGroupMessagesCount(user.getUsername(), ou));
    }

    @PostMapping("/user/{uid}/new/{uuid}")
    @Operation(summary = "Mark user message as received")
    ResponseEntity<Void> receiveNewUserMessage(@PathVariable(value = "uid") @Parameter(description = "Uid of other user") String uid,
                                               @PathVariable(value = "uuid") @Parameter(description = "Number of message") UUID uuid,
                                               @AuthenticationPrincipal UserDetails user) {
        manageMessage.markUserMessageReceived(user.getUsername(), uid, uuid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/group/{ou}/new/{uuid}")
    @Operation(summary = "Mark group message as received")
    ResponseEntity<Void> receiveNewGroupMessage(@PathVariable(value = "ou") @Parameter(description = "ou of group") String ou,
                                                @PathVariable(value = "uuid") @Parameter(description = "Number of message") UUID uuid,
                                                @AuthenticationPrincipal UserDetails user) {
        manageMessage.markGroupMessageReceived(user.getUsername(), ou, uuid);
        return ResponseEntity.ok().build();
    }
}
