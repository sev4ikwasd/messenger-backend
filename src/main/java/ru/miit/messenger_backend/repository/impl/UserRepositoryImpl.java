package ru.miit.messenger_backend.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.ldap.core.LdapClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.miit.messenger_backend.dto.request.OneTimeKey;
import ru.miit.messenger_backend.dto.request.RegisterUser;
import ru.miit.messenger_backend.dto.response.PreKeyBundle;
import ru.miit.messenger_backend.dto.response.UserInfo;
import ru.miit.messenger_backend.dto.response.UserVault;
import ru.miit.messenger_backend.entity.UserDataEntity;
import ru.miit.messenger_backend.entity.UserEntity;
import ru.miit.messenger_backend.entity.UserOneTimeKeyEntity;
import ru.miit.messenger_backend.exception.UserNotFoundByUidException;
import ru.miit.messenger_backend.repository.UserRepository;

import javax.naming.directory.Attributes;
import java.util.List;
import java.util.Map;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Repository
@RequiredArgsConstructor
@Log
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final LdapClient ldapClient;
    private final RowMapper<UserEntity> userEntityRowMapper = (rs, rowNum) -> UserEntity.builder()
            .id(rs.getInt("id"))
            .uid(rs.getString("uid"))
            .lastVisited(rs.getTimestamp("last_visited").toLocalDateTime())
            .masterPasswordHash(rs.getBytes("master_password_hash"))
            .protectedSymmetricKey(rs.getBytes("protected_symmetric_key"))
            .identityPublicKey(rs.getBytes("identity_public_key"))
            .signedPublicKey(rs.getBytes("signed_public_key"))
            .build();
    private final RowMapper<UserOneTimeKeyEntity> userOneTimeKeyRowMapper = (rs, rowNum) -> UserOneTimeKeyEntity.builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .publicOneTimeKey(rs.getBytes("public_one_time_key"))
            .keyNumber(rs.getInt("key_number"))
            .isUsed(rs.getBoolean("is_used"))
            .build();
    private final RowMapper<UserDataEntity> userDataEntityRowMapper = (rs, rowNum) -> UserDataEntity.builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .encryptedData(rs.getBytes("encrypted_data"))
            .startDatetime(rs.getTimestamp("start_datetime").toLocalDateTime())
            .endDatetime(rs.getTimestamp("end_datetime").toLocalDateTime())
            .build();
    @Value("${ldap.user_filter}")
    String ldapUserFilter;
    @Value("${ldap.user_attribute}")
    String ldapUserAttribute;

    @Override
    public int getIdByUid(String uid) {
        List<String> uids = ldapClient.search()
                .query(query()
                        .filter(ldapUserFilter, uid))
                .toList((Attributes attrs) -> (String) attrs.get("uid").get());
        if (uids.isEmpty()) throw new UserNotFoundByUidException(uid);
        List<Integer> idList = jdbcTemplate.queryForList("SELECT id FROM messenger.user WHERE uid = :user_uid LIMIT 1", Map.of("user_uid", uid), Integer.class);
        Integer id;
        if (idList.isEmpty()) {
            log.warning("User with uid: " + uid + " not found");
            throw new UserNotFoundByUidException("User not found error");
        } else {
            id = idList.getFirst();
        }
        return id;
    }

    @Override
    public UserInfo getUserInfo(String uid) {
        String name = ldapClient.search()
                .query(query()
                        .filter(ldapUserFilter, uid))
                .toList((Attributes attrs) -> (String) attrs.get("cn").get())
                .getFirst();
        return new UserInfo(name);
    }

    @Override
    @Transactional
    public PreKeyBundle getPreKeyBundle(String uid) {
        int userId = getIdByUid(uid);
        List<UserEntity> userEntity = jdbcTemplate.query("SELECT * FROM messenger.user WHERE id = :user_id LIMIT 1", Map.of("user_id", userId), userEntityRowMapper);
        if (userEntity.isEmpty()) {
            log.warning("User with id: " + userId + " not found");
            throw new RuntimeException("User not found error");
        }
        List<UserOneTimeKeyEntity> userOneTimeKeyList = jdbcTemplate.query("SELECT * FROM messenger.user_one_time_key WHERE user_id = :user_id AND NOT is_used", Map.of("user_id", userId), userOneTimeKeyRowMapper);
        Byte[] oneTimeKey = null;
        if (!userOneTimeKeyList.isEmpty()) {
            UserOneTimeKeyEntity userOneTimeKeyEntity = userOneTimeKeyList.getFirst();
            oneTimeKey = new Byte[userOneTimeKeyEntity.getPublicOneTimeKey().length];
            for (int i = 0; i < userOneTimeKeyEntity.getPublicOneTimeKey().length; i++) {
                oneTimeKey[i] = userOneTimeKeyEntity.getPublicOneTimeKey()[i];
            }
            jdbcTemplate.update("DELETE FROM messenger.user_one_time_key WHERE id = :id",
                    Map.of("id", userOneTimeKeyEntity.getId()));
        }
        return new PreKeyBundle(userEntity.getFirst().getIdentityPublicKey(), userEntity.getFirst().getSignedPublicKey(), oneTimeKey);
    }

    @Override
    public UserVault getUserVault(String uid) {
        int userId = getIdByUid(uid);
        List<UserDataEntity> userDataEntity = jdbcTemplate.query("SELECT * FROM messenger.user_data WHERE id = :user_id",
                Map.of("user_id", userId), userDataEntityRowMapper);

        if (userDataEntity.isEmpty()) {
            log.warning("User with id: " + userId + " not found");
            throw new RuntimeException("User not found error");
        }

        return new UserVault(userDataEntity.getFirst().getEncryptedData());
    }

    @Override
    @Transactional
    public void updateUserVault(String uid, byte[] vaultUpdate) {
        int userId = getIdByUid(uid);
        List<UserDataEntity> userDataEntity = jdbcTemplate.query("SELECT * FROM messenger.user_data WHERE id = :user_id",
                Map.of("user_id", userId), userDataEntityRowMapper);

        if (userDataEntity.isEmpty()) {
            log.warning("User with id: " + userId + " not found");
            throw new RuntimeException("User not found error");
        }

        jdbcTemplate.update("UPDATE messenger.user_data SET (encrypted_data) VALUES (:data) WHERE id = :user_id",
                Map.of("user_id", userId,
                        "data", vaultUpdate));
    }

    @Override
    @Transactional
    public void registerNewUser(String uid, RegisterUser data) {
        jdbcTemplate.update("INSERT INTO messenger.user (uid, last_visited, master_password_hash, protected_symmetric_key, identity_public_key, signed_public_key) VALUES (:uid, CURRENT_TIMESTAMP, :master_password_hash, :protected_symmetric_key, :identity_public_key, :signed_public_key)",
                Map.of("uid", uid,
                        "master_password_hash", data.masterPasswordHash(),
                        "protected_symmetric_key", data.protectedSymmetricKey(),
                        "identity_public_key", data.identityPublicKey(),
                        "signed_public_key", data.signedPublicKey()));

        int id = getIdByUid(uid);

        jdbcTemplate.update("INSERT INTO messenger.user_data (id, encrypted_data) VALUES (:id, :encrypted_data)",
                Map.of("id", id,
                        "encrypted_data", new byte[]{}));

        for (OneTimeKey key : data.publicOneTimeKeyList()) {
            jdbcTemplate.update("INSERT INTO messenger.user_one_time_key (user_id, public_one_time_key, key_number, is_used) VALUES (:user_id, :public_one_time_key, :key_number, :is_used)",
                    Map.of("user_id", id,
                            "public_one_time_key", key.key(),
                            "key_number", key.keyNumber(),
                            "is_used", false));
        }
    }
}
