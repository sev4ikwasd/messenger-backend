package ru.miit.messenger_backend.application.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@EqualsAndHashCode
@AggregateRoot
@Table(value = "user_data", schema = "messenger")
public class UserData {
    @Identity
    @Id
    private Integer id;
    @Setter
    private byte[] userData;

    public UserData(byte[] userData) {
        this.userData = userData;
    }

    @PersistenceCreator
    public UserData(byte[] userData, Integer id) {
        this.userData = userData;
        this.id = id;
    }
}
