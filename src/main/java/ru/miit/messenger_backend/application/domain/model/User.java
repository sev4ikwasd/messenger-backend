package ru.miit.messenger_backend.application.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import ru.miit.messenger_backend.exception.BusinessRuleViolationException;
import ru.miit.messenger_backend.utils.Constants;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@AggregateRoot
@Table(value = "user", schema = "messenger")
public class User {
    private final String uid;
    private final LocalDateTime lastVisited;
    private final byte[] identityPublicKey;
    private final byte[] signedPublicKey;
    @MappedCollection(idColumn = "id_user")
    private final Set<UserOneTimeKey> userOneTimeKeys;
    @Identity
    @Id
    private Integer id;

    public User(String uid, LocalDateTime lastVisited, byte[] identityPublicKey, byte[] signedPublicKey, Set<UserOneTimeKey> userOneTimeKeys) {
        if (userOneTimeKeys.size() != Constants.USER_MAX_ONE_TIME_KEY_COUNT)
            throw new BusinessRuleViolationException("Incorrect amount of one time keys");
        if (!userOneTimeKeys.stream().map(UserOneTimeKey::getKeyNumber).allMatch(new HashSet<>()::add))
            throw new BusinessRuleViolationException("One time key numbers not unique");
        this.uid = uid;
        this.lastVisited = lastVisited;
        this.identityPublicKey = identityPublicKey;
        this.signedPublicKey = signedPublicKey;
        this.userOneTimeKeys = userOneTimeKeys;
    }

    @PersistenceCreator
    public User(Integer id, String uid, LocalDateTime lastVisited, byte[] identityPublicKey, byte[] signedPublicKey, Set<UserOneTimeKey> userOneTimeKeys) {
        this.id = id;
        this.uid = uid;
        this.lastVisited = lastVisited;
        this.identityPublicKey = identityPublicKey;
        this.signedPublicKey = signedPublicKey;
        this.userOneTimeKeys = userOneTimeKeys;
    }

    public boolean isRequiresReplenishingKeys() {
        return userOneTimeKeys.size() <= Constants.USER_MIN_ONE_TIME_KEY_COUNT;
    }

    public void replenishOneTimeKeys(Set<UserOneTimeKey> keys) {
        int requiredCount = Constants.USER_MAX_ONE_TIME_KEY_COUNT - userOneTimeKeys.size();
        if (keys.size() != requiredCount) throw new BusinessRuleViolationException("Incorrect key count");
        if (!Collections.disjoint(keys.stream().map(UserOneTimeKey::getKeyNumber).collect(Collectors.toSet()), userOneTimeKeys.stream().map(UserOneTimeKey::getKeyNumber).collect(Collectors.toSet())))
            throw new BusinessRuleViolationException("Key numbers are not updated");
        userOneTimeKeys.addAll(keys);
    }

    public Optional<UserOneTimeKey> takeOneTimeKey() {
        if (userOneTimeKeys.isEmpty()) return Optional.empty();
        UserOneTimeKey userOneTimeKey = userOneTimeKeys.iterator().next();
        userOneTimeKeys.remove(userOneTimeKey);
        return Optional.of(userOneTimeKey);
    }
}
