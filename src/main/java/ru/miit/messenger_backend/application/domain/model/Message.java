package ru.miit.messenger_backend.application.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.miit.messenger_backend.exception.BusinessRuleViolationException;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@AggregateRoot
@Table(value = "message", schema = "messenger")
public class Message {
    private final UUID messageNumber;
    private final AggregateReference<User, Integer> idSender;
    private final AggregateReference<User, Integer> idReceiver;
    private final byte[] message;
    private final String ouGroup;
    private final LocalDateTime time_sent;
    @Identity
    @Id
    private Integer id;
    @Column("id_state")
    private MessageState messageState;

    public Message(UUID messageNumber, AggregateReference<User, Integer> idSender, AggregateReference<User, Integer> idReceiver, byte[] message, String ouGroup, LocalDateTime time_sent) {
        this.messageNumber = messageNumber;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.message = message;
        this.ouGroup = ouGroup;
        this.messageState = MessageState.SENT;
        this.time_sent = time_sent;
    }

    @PersistenceCreator
    public Message(Integer id, UUID messageNumber, AggregateReference<User, Integer> idSender, AggregateReference<User, Integer> idReceiver, byte[] message, String ouGroup, MessageState messageState, LocalDateTime time_sent) {
        this.id = id;
        this.messageNumber = messageNumber;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.message = message;
        this.ouGroup = ouGroup;
        this.messageState = messageState;
        this.time_sent = time_sent;
    }

    public void receiveMessage() {
        if (messageState == MessageState.RECEIVED) throw new BusinessRuleViolationException("Message already received");
        messageState = MessageState.RECEIVED;
    }
}
