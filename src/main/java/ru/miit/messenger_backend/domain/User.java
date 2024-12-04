package ru.miit.messenger_backend.domain;

import lombok.Data;

import java.security.Principal;

@Data
public class User implements Principal {
    private String uid;
    private String name;
}