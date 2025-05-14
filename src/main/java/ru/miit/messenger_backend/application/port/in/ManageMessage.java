package ru.miit.messenger_backend.application.port.in;

import org.jmolecules.architecture.hexagonal.PrimaryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.miit.messenger_backend.application.domain.dto.ChatsDto;
import ru.miit.messenger_backend.application.domain.dto.MessageDto;
import ru.miit.messenger_backend.application.domain.dto.SendMessageDto;

import java.util.UUID;

@PrimaryPort
public interface ManageMessage {
    void sendMessage(String uidSender, SendMessageDto sendMessage);

    Page<ChatsDto> getChats(String uid, Pageable pageable);

    Page<MessageDto> getOldUserMessages(String uid, String otherUserUid, Pageable pageable);

    Page<MessageDto> getOldGroupMessages(String uid, String ou, Pageable pageable);

    Page<MessageDto> getNewUserMessages(String uid, String otherUserUid, Pageable pageable);

    Page<MessageDto> getNewGroupMessages(String uid, String ou, Pageable pageable);

    int newUserMessagesCount(String uid, String otherUserUid);

    int newGroupMessagesCount(String uid, String ou);

    void markUserMessageReceived(String uid, String otherUserUid, UUID messageNumber);

    void markGroupMessageReceived(String uid, String ou, UUID messageNumber);
}
