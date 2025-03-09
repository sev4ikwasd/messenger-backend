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
public class LdapGroup {
    @Identity
    private final String ou;
    private final String name;
}