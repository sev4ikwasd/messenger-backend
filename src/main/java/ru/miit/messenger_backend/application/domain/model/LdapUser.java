package ru.miit.messenger_backend.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@AggregateRoot
public class LdapUser {
    @Identity
    private final String uid;
    private final String name;
}
