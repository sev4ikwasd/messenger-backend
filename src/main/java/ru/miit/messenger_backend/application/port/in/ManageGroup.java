package ru.miit.messenger_backend.application.port.in;

import org.jmolecules.architecture.hexagonal.PrimaryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.miit.messenger_backend.application.domain.dto.GroupDto;
import ru.miit.messenger_backend.application.domain.dto.UserInfoDto;

@PrimaryPort
public interface ManageGroup {
    Page<GroupDto> getAvailableGroups(String uid, Pageable pageable);

    Page<UserInfoDto> getGroupUsers(String uid, String ou, Pageable pageable);
}
