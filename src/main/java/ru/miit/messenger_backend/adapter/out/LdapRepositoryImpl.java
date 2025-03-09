package ru.miit.messenger_backend.adapter.out;

import lombok.RequiredArgsConstructor;
import org.jmolecules.architecture.hexagonal.SecondaryAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapClient;
import org.springframework.stereotype.Repository;
import ru.miit.messenger_backend.application.domain.model.LdapGroup;
import ru.miit.messenger_backend.application.domain.model.LdapUser;
import ru.miit.messenger_backend.application.port.out.LdapRepository;

import javax.naming.directory.Attributes;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@SecondaryAdapter
@Repository
@RequiredArgsConstructor
public class LdapRepositoryImpl implements LdapRepository {
    private final LdapClient ldapClient;

    @Value("${ldap.user_filter}")
    private String ldapUserFilter;
    @Value("${ldap.user_attribute}")
    private String ldapUserAttribute;
    @Value("${ldap.uid}")
    private String ldapUid;

    @Override
    public Optional<LdapUser> getUserByUid(String uid) {
        List<String> nameList = ldapClient.search()
                .query(query()
                        .filter(ldapUserFilter, uid))
                .toList((Attributes attrs) -> (String) attrs.get(ldapUserAttribute).get());
        if (nameList.isEmpty()) return Optional.empty();
        return Optional.of(new LdapUser(uid, nameList.getFirst()));
    }

    @Override
    public Optional<LdapGroup> getGroupByOu(String ou) {
        List<LdapGroup> group = ldapClient.search()
                .query(query()
                        .where("objectClass").is("groupOfUniqueNames")
                        .and("ou").is(ou))
                .toList((Attributes attrs) -> new LdapGroup(ou, attrs.get(ldapUserAttribute).get().toString())).stream()
                .toList();
        if (group.isEmpty()) return Optional.empty();
        return Optional.of(group.getFirst());
    }

    @Override
    public List<LdapUser> getAllUsers() {
        return ldapClient.search()
                .query(query()
                        .filter("objectClass={0}", "organizationalPerson"))
                .toList((Attributes attrs) -> new LdapUser((String) attrs.get(ldapUid).get(), (String) attrs.get(ldapUserAttribute).get()));
    }

    @Override
    public List<LdapGroup> getAllGroupForUser(String uid) {
        return ldapClient.search()
                .query(query()
                        .filter("objectClass={0}", "groupOfUniqueNames"))
                .toList((Attributes attrs) -> {
                    String ou = attrs.get("ou").get().toString();
                    List<String> groupDns = (List<String>) Collections.list(attrs.get("uniqueMember").getAll());
                    boolean isInGroup = false;
                    for (String groupDn : groupDns) {
                        if (groupDn.contains(ldapUid + "=" + uid)) {
                            isInGroup = true;
                            break;
                        }
                    }
                    if (isInGroup)
                        return Optional.of(new LdapGroup(ou, attrs.get(ldapUserAttribute).get().toString()));
                    return Optional.empty();
                }).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(val -> (LdapGroup) val)
                .toList();
    }

    @Override
    public List<LdapUser> getAllUsersForGroup(String ou) {
        return ldapClient.search()
                .query(query()
                        .where("objectClass").is("groupOfUniqueNames")
                        .and("ou").is(ou))
                .toList((Attributes attrs) -> {
                    List<String> groupDns = (List<String>) Collections.list(attrs.get("uniqueMember").getAll());
                    List<LdapUser> users = new ArrayList<>();
                    for (String groupDn : groupDns) {
                        String[] split = Arrays.stream(groupDn.split(","))
                                .filter(s -> s.startsWith(ldapUid))
                                .collect(Collectors.joining())
                                .split("=");
                        if (split.length < 2) continue;
                        String uid = split[1];
                        Optional<LdapUser> ldapUser = getUserByUid(uid);
                        ldapUser.ifPresent(users::add);
                    }
                    return users;
                }).getFirst();
    }
}
