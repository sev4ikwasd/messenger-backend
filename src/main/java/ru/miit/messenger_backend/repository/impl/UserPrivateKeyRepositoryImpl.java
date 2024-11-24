package ru.miit.messenger_backend.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miit.messenger_backend.entity.UserEntity;
import ru.miit.messenger_backend.entity.UserPrivateKeyEntity;
import ru.miit.messenger_backend.repository.UserPrivateKeyRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class UserPrivateKeyRepositoryImpl implements UserPrivateKeyRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<UserPrivateKeyEntity> userPrivateKeyEntityRowMapper = (rs, rowNum) -> UserPrivateKeyEntity.builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .encryptedKey(rs.getBytes("encrypted_key"))
            .build();

    @Override
    public void createUserPrivateKeyEntity(final UserPrivateKeyEntity userPrivateKeyEntity) {
        String sql = "INSERT INTO messenger.user_private_key (user_id, encrypted_key) " +
                "VALUES (:userId, :encryptedKey)";

        Map<String, Object> params = generateMapParameters(userPrivateKeyEntity);


        jdbcTemplate.update(sql, params);
    }

    @Override
    public UserPrivateKeyEntity getUserPrivateKeyEntity(final int userPrivateKeyId) {
        String sql = "SELECT * FROM messenger.user_private_key WHERE id = :id";
        Map<String, Object> params = new HashMap<>(Map.of(
                "id", userPrivateKeyId));

        return jdbcTemplate.queryForObject(sql, params, userPrivateKeyEntityRowMapper);
    }

    @Override
    public void updateUserPrivateKeyEntity(UserPrivateKeyEntity userPrivateKeyEntity) {
        String sql = "UPDATE messenger.user_private_key SET user_id = :userId, encrypted_key = :encryptedKey " +
                "WHERE id = :id";
        Map<String, Object> params = generateMapParameters(userPrivateKeyEntity);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteUserPrivateKeyEntity(int userPrivateKeyId) {
        String sql = "DELETE FROM messenger.user_private_key WHERE id = :id";
        Map<String, Object> params = new HashMap<>(Map.of(
                "id", userPrivateKeyId));

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<UserPrivateKeyEntity> getAllUserEntities() {
        String sql = "SELECT * FROM messenger.user_private_key";

        return jdbcTemplate.query(sql, userPrivateKeyEntityRowMapper);
    }

    private Map<String, Object> generateMapParameters(final UserPrivateKeyEntity userPrivateKeyEntity) {
        return Map.of(
                "id", userPrivateKeyEntity.getId(),
                "user_id", userPrivateKeyEntity.getUserId(),
                "encrypted_key", userPrivateKeyEntity.getEncryptedKey()
        );
    }
}
