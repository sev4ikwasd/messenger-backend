package ru.miit.messenger_backend.application.domain.service;

import lombok.RequiredArgsConstructor;
import org.jmolecules.architecture.hexagonal.Application;
import org.jmolecules.ddd.annotation.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.miit.messenger_backend.application.domain.dto.GroupDto;
import ru.miit.messenger_backend.application.domain.dto.UserInfoDto;
import ru.miit.messenger_backend.application.domain.model.LdapUser;
import ru.miit.messenger_backend.application.domain.model.User;
import ru.miit.messenger_backend.application.port.in.ManageGroup;
import ru.miit.messenger_backend.application.port.out.LdapRepository;
import ru.miit.messenger_backend.application.port.out.UserRepository;
import ru.miit.messenger_backend.exception.BusinessRuleViolationException;
import ru.miit.messenger_backend.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Application
@Service
public class GroupService implements ManageGroup {
    private final LdapRepository ldapRepository;
    private final UserRepository userRepository;

    @Override
    public Page<GroupDto> getAvailableGroups(String uid, Pageable pageable) {
        return Utils.paginate(ldapRepository.getAllGroupForUser(uid).stream()
                        .map(ldapGroup -> new GroupDto(ldapGroup.getOu(), ldapGroup.getName()))
                        .collect(Collectors.toList()),
                pageable);
    }

    @Override
    public Page<UserInfoDto> getGroupUsers(String uid, String ou, Pageable pageable) {
        List<LdapUser> ldapUserList = ldapRepository.getAllUsersForGroup(ou);
        boolean inGroup = false;
        List<UserInfoDto> userInfoDtoList = new ArrayList<>();
        for (LdapUser ldapUser : ldapUserList) {
            if (ldapUser.getUid().equals(uid)) inGroup = true;
            Optional<User> userOptional = userRepository.getUserByUid(ldapUser.getUid());
            userInfoDtoList.add(new UserInfoDto(ldapUser.getUid(), ldapUser.getName(), userOptional.isPresent()));
        }
        if (!inGroup) throw new BusinessRuleViolationException("User not in group");
        return Utils.paginate(userInfoDtoList, pageable);
    }
}
