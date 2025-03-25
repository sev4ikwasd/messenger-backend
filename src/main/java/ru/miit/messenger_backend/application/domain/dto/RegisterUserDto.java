package ru.miit.messenger_backend.application.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.miit.messenger_backend.utils.Constants;

import java.util.List;

public record RegisterUserDto(@NotEmpty byte[] identityPublicKey,
                              @NotEmpty byte[] signedPublicKey,
                              @NotNull @NotEmpty @Size(min = Constants.USER_MAX_ONE_TIME_KEY_COUNT, max = Constants.USER_MAX_ONE_TIME_KEY_COUNT) List<OneTimeKeyDto> oneTimeKeyList,
                              @NotEmpty byte[] userData,
                              @NotEmpty byte[] protectedSymmetricKey) {
}
