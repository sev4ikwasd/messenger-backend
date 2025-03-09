package ru.miit.messenger_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import ru.miit.messenger_backend.application.domain.model.MessageStateReadConverter;
import ru.miit.messenger_backend.application.domain.model.MessageStateWriteConverter;

import java.util.List;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(List.of(new MessageStateWriteConverter(), new MessageStateReadConverter()));
    }
}
