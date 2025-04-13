package ru.miit.messenger_backend.application.port.out;

import org.jmolecules.architecture.hexagonal.SecondaryPort;
import org.jmolecules.ddd.annotation.Repository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.miit.messenger_backend.application.domain.model.Message;

import java.util.List;

@Repository
@org.springframework.stereotype.Repository
@SecondaryPort
public interface MessageRepository extends CrudRepository<Message, Integer> {
    @Query("SELECT DISTINCT id_sender FROM messenger.message WHERE ou_group IS NULL AND (id_sender = :id OR id_receiver = :id)")
    List<Integer> getUserChats(int id);

    @Query("SELECT DISTINCT ou_group FROM messenger.message WHERE ou_group IS NOT NULL AND (id_sender = :id OR id_receiver = :id)")
    List<String> getGroupChats(int id);

    @Query("SELECT * FROM messenger.message WHERE ou_group IS NULL AND ((id_sender = :id AND id_receiver = :otherUserId) OR (id_sender = :otherUserId AND id_receiver = :id)) AND (((id_state = 1) AND (id_receiver = :id)) OR (id_receiver <> :id)) ORDER BY time_sent DESC LIMIT :size OFFSET :offset")
    List<Message> getOldUserChatMessages(int id, int otherUserId, int size, int offset);

    @Query("SELECT count(*) FROM messenger.message WHERE ou_group IS NULL AND ((id_sender = :id AND id_receiver = :otherUserId) OR (id_sender = :otherUserId AND id_receiver = :id)) AND (((id_state = 1) AND (id_receiver = :id)) OR (id_receiver <> :id))")
    int getOldUserChatMessagesSize(int id, int otherUserId);

    @Query("SELECT * FROM messenger.message WHERE ou_group = :ou AND (((id_state = 1) AND (id_receiver = :id)) OR (id_receiver <> :id)) ORDER BY time_sent DESC LIMIT :size OFFSET :offset")
    List<Message> getOldUserGroupMessages(int id, String ou, int size, int offset);

    @Query("SELECT count(*) FROM messenger.message WHERE ou_group = :ou AND (((id_state = 1) AND (id_receiver = :id)) OR (id_receiver <> :id))")
    int getOldUserGroupMessagesSize(int id, String ou);

    @Query("SELECT * FROM messenger.message WHERE ou_group IS NULL AND ((id_sender = :id AND id_receiver = :otherUserId) OR (id_sender = :otherUserId AND id_receiver = :id)) AND id_state = 0 ORDER BY time_sent ASC LIMIT :size OFFSET :offset")
    List<Message> getNewUserChatMessages(int id, int otherUserId, int size, int offset);

    @Query("SELECT count(*) FROM messenger.message WHERE ou_group IS NULL AND ((id_sender = :id AND id_receiver = :otherUserId) OR (id_sender = :otherUserId AND id_receiver = :id)) AND id_state = 0")
    int getNewUserChatMessagesSize(int id, int otherUserId);

    @Query("SELECT * FROM messenger.message WHERE ou_group = :ou AND id_state = 0 AND (id_receiver = :id OR id_receiver <> :id) ORDER BY time_sent ASC LIMIT :size OFFSET :offset")
    List<Message> getNewUserGroupMessages(int id, String ou, int size, int offset);

    @Query("SELECT count(*) FROM messenger.message WHERE ou_group = :ou AND id_state = 0 AND (id_receiver = :id OR id_receiver <> :id)")
    int getNewUserGroupMessagesSize(int id, String ou);
}
