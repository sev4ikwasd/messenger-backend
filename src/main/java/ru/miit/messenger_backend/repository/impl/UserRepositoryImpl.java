package ru.miit.messenger_backend.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miit.messenger_backend.entity.UserEntity;
import ru.miit.messenger_backend.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<UserEntity> userEntityRowMapper = (rs, rowNum) -> UserEntity.builder()
            .id(rs.getInt("id"))
            .uid(rs.getInt("uid"))
            .lastVisited(rs.getTimestamp("last_visited").toLocalDateTime())
            .masterPasswordHash(rs.getBytes("master_password_hash"))
            .protectedSymmetricKey(rs.getBytes("protected_symmetric_key"))
            .build();

    @Override
    public void createUserEntity(final UserEntity userEntity) {
        String sql = "INSERT INTO messenger.user (uid, last_visited, master_password_hash, protected_symmetric_key) " +
                "VALUES (:uid, :lastVisited, :masterPasswordHash, :protectedSymmetricKey)";

        Map<String, Object> params = generateMapParameters(userEntity);


        jdbcTemplate.update(sql, params);

    }


    @Override
    public UserEntity getUserEntityById(final int userId) {
        String sql = "SELECT * FROM messenger.user WHERE id = :id";
        Map<String, Object> params = new HashMap<>(Map.of(
                "id", userId));

        return jdbcTemplate.queryForObject(sql, params, userEntityRowMapper);
    }

    @Override
    public void updateUserEntity(final UserEntity userEntity) {
        String sql = "UPDATE messenger.user SET uid = :uid, last_visited = :lastVisited, master_password_hash = :masterPasswordHash, protected_symmetric_key = :protectedSymmetricKey " +
                "WHERE id = :id";
        Map<String, Object> params = generateMapParameters(userEntity);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteUserEntity(final int userId) {
        String sql = "DELETE FROM messenger.user WHERE id = :id";
        Map<String, Object> params = new HashMap<>(Map.of(
                "id", userId));

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<UserEntity> getAllUserEntities() {
        String sql = "SELECT * FROM messenger.user";

        return jdbcTemplate.query(sql, userEntityRowMapper);
    }

    private Map<String, Object> generateMapParameters(final UserEntity userEntity) {
        return Map.of(
                "id", userEntity.getId(),
                "uid", userEntity.getUid(),
                "lastVisited", userEntity.getLastVisited(),
                "masterPasswordHash", userEntity.getMasterPasswordHash(),
                "protectedSymmetricKey", userEntity.getProtectedSymmetricKey()
        );
    }
}
