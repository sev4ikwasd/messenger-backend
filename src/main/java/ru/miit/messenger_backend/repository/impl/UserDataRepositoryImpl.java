package ru.miit.messenger_backend.repository.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.miit.messenger_backend.entity.UserDataEntity;
import ru.miit.messenger_backend.repository.UserDataRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class UserDataRepositoryImpl implements UserDataRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<UserDataEntity> userDataEntityRowMapper = (rs, rowNum) -> UserDataEntity.builder()
            .id(rs.getInt("id"))
            .userId(rs.getInt("user_id"))
            .encryptedData(rs.getBytes("encrypted_data"))
            .startDatetime(rs.getTimestamp("start_datetime").toLocalDateTime())
            .endDatetime(rs.getTimestamp("end_datetime").toLocalDateTime())
            .build();

    @Override
    public void createUserDataEntity(final UserDataEntity userDataEntity) {
        String sql = "INSERT INTO messenger.user_data (user_id, encrypted_data, start_datetime, end_datetime) " +
                "VALUES (:id, :userId, :encryptedData, :startDatetime, :endDatetime)";

        Map<String, Object> params = generateMapParameters(userDataEntity);


        jdbcTemplate.update(sql, params);
    }

    @Override
    public UserDataEntity getUserDataEntity(final int userDataId) {
        String sql = "SELECT * FROM messenger.user_data WHERE id = :id";
        Map<String, Object> params = new HashMap<>(Map.of(
                "id", userDataId));

        return jdbcTemplate.queryForObject(sql, params, userDataEntityRowMapper);
    }

    @Override
    public void updateUserDataEntity(final UserDataEntity userDataEntity) {
        String sql = "UPDATE messenger.user_data SET user_id = :userId, encrypted_data = :encryptedData, start_datetime = :startDatetime, end_datetime = :endDatetime " +
                "WHERE id = :id";
        Map<String, Object> params = generateMapParameters(userDataEntity);

        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteUserDataEntity(final int userDataId) {
        String sql = "DELETE FROM messenger.user_data WHERE id = :id";
        Map<String, Object> params = new HashMap<>(Map.of(
                "id", userDataId));

        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<UserDataEntity> getAllUserDataEntities() {
        String sql = "SELECT * FROM messenger.user_data";

        return jdbcTemplate.query(sql, userDataEntityRowMapper);
    }

    private Map<String, Object> generateMapParameters(final UserDataEntity userDataEntity) {
        return Map.of(
                "id", userDataEntity.getId(),
                "userId", userDataEntity.getUserId(),
                "encryptedData", userDataEntity.getEncryptedData(),
                "startDatetime", userDataEntity.getStartDatetime(),
                "endDatetime", userDataEntity.getEndDatetime()
        );
    }
}
