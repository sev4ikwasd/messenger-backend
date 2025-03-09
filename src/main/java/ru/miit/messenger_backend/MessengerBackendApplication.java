package ru.miit.messenger_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication
@EnableWebSecurity
@EnableWebSocketMessageBroker
@EnableJdbcRepositories
@EnableAutoConfiguration
public class MessengerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessengerBackendApplication.class, args);
    }

}
