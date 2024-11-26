package ru.miit.messenger_backend.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Principal;

@Getter
@AllArgsConstructor
public class User implements Principal {
    private final String name;
}
