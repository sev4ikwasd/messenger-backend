package ru.miit.messenger_backend.application.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@Entity
@Table(value = "user_one_time_key", schema = "messenger")
public class UserOneTimeKey {
    private final byte[] publicOneTimeKey;
    private final UUID keyNumber;
    @Identity
    @Id
    private Integer id;

    public UserOneTimeKey(byte[] publicOneTimeKey, UUID keyNumber) {
        this.publicOneTimeKey = publicOneTimeKey;
        this.keyNumber = keyNumber;
    }

    @PersistenceCreator
    public UserOneTimeKey(int id, byte[] publicOneTimeKey, UUID keyNumber) {
        this.id = id;
        this.publicOneTimeKey = publicOneTimeKey;
        this.keyNumber = keyNumber;
    }
}
