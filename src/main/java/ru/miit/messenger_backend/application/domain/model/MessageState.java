package ru.miit.messenger_backend.application.domain.model;

import lombok.Getter;
import org.jmolecules.ddd.annotation.ValueObject;
import ru.miit.messenger_backend.exception.ResourceNotFoundException;

import java.util.Arrays;

@ValueObject
@Getter
public enum MessageState {
    SENT(0), RECEIVED(1);

    private final int id;

    MessageState(int id) {
        this.id = id;
    }

    public static MessageState fromId(int id) {
        return Arrays.stream(MessageState.values())
                .filter(messageState -> messageState.getId() == id)
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException("Message state not found"));
    }
}
