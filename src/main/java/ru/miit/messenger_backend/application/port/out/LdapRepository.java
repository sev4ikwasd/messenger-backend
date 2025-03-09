package ru.miit.messenger_backend.application.port.out;

import org.jmolecules.architecture.hexagonal.SecondaryPort;
import org.jmolecules.ddd.annotation.Repository;
import ru.miit.messenger_backend.application.domain.model.LdapGroup;
import ru.miit.messenger_backend.application.domain.model.LdapUser;

import java.util.List;
import java.util.Optional;

@Repository
@SecondaryPort
public interface LdapRepository {
    Optional<LdapUser> getUserByUid(String uid);

    Optional<LdapGroup> getGroupByOu(String ou);

    List<LdapUser> getAllUsers();

    List<LdapGroup> getAllGroupForUser(String uid);

    List<LdapUser> getAllUsersForGroup(String ou);
}
