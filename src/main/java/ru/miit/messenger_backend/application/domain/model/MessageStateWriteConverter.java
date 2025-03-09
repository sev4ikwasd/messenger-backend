package ru.miit.messenger_backend.application.domain.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;

import java.sql.JDBCType;

@WritingConverter
public class MessageStateWriteConverter implements Converter<MessageState, JdbcValue> {
    @Override
    public JdbcValue convert(MessageState source) {
        return JdbcValue.of(source.getId(), JDBCType.INTEGER);
    }
}
