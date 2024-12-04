package ru.miit.messenger_backend.repository.impl;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.miit.messenger_backend.dto.response.PreKeyBundle;
import ru.miit.messenger_backend.dto.response.UserVault;
import ru.miit.messenger_backend.entity.UserDataEntity;
import ru.miit.messenger_backend.entity.UserEntity;
import ru.miit.messenger_backend.entity.UserOneTimeKeyEntity;
import ru.miit.messenger_backend.exception.UserNotFoundByUidException;
import ru.miit.messenger_backend.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
@Log
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<UserEntity> userEntityRowMapper = (rs, rowNum) -> UserEntity.builder()
            .id(rs.getInt("id"))
            .uid(rs.getInt("uid"))
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

    @Override
    public int getIdByUid(String uid) {
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM messenger.user WHERE uid = :user_uid LIMIT 1", Map.of("user_uid", uid), Integer.class);
        if (id == null) throw new UserNotFoundByUidException(uid);
        return id;
    }

    @Override
    @Transactional
    public PreKeyBundle getPreKeyBundle(int userId) {
        UserEntity userEntity = jdbcTemplate.queryForObject("SELECT * FROM messenger.user WHERE id = :user_id LIMIT 1", Map.of("user_id", userId), userEntityRowMapper);
        if (userEntity == null) {
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
        return new PreKeyBundle(userEntity.getIdentityPublicKey(), userEntity.getSignedPublicKey(), oneTimeKey);
    }

    @Override
    public UserVault getUserVault(int userId) {
        UserDataEntity userDataEntity = jdbcTemplate.queryForObject("SELECT * FROM messenger.user_data WHERE id = :user_id",
                Map.of("user_id", userId), userDataEntityRowMapper);

        if (userDataEntity == null) {
            log.warning("User with id: " + userId + " not found");
            throw new RuntimeException("User not found error");
        }

        return new UserVault(userDataEntity.getEncryptedData());
    }

    @Override
    @Transactional
    public void updateUserVault(int userId, byte[] vaultUpdate) {
        UserDataEntity userDataEntity = jdbcTemplate.queryForObject("SELECT * FROM messenger.user_data WHERE id = :user_id",
                Map.of("user_id", userId), userDataEntityRowMapper);

        if (userDataEntity == null) {
            log.warning("User with id: " + userId + " not found");
            throw new RuntimeException("User not found error");
        }

        jdbcTemplate.update("UPDATE messenger.user_date SET (encrypted_data) VALUES (:data) WHERE id = :user_id",
                Map.of("user_id", userId,
                        "data", vaultUpdate));
    }
}
