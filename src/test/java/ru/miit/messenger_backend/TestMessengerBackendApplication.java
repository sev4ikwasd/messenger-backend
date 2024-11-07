package ru.miit.messenger_backend;

import org.springframework.boot.SpringApplication;

public class TestMessengerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(MessengerBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
