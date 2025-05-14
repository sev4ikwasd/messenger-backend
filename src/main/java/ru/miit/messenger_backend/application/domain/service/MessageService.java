package ru.miit.messenger_backend.application.domain.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jmolecules.architecture.hexagonal.Application;
import org.jmolecules.ddd.annotation.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.miit.messenger_backend.application.domain.dto.*;
import ru.miit.messenger_backend.application.domain.model.*;
import ru.miit.messenger_backend.application.port.in.ManageMessage;
import ru.miit.messenger_backend.application.port.out.LdapRepository;
import ru.miit.messenger_backend.application.port.out.MessageRepository;
import ru.miit.messenger_backend.application.port.out.UserRepository;
import ru.miit.messenger_backend.exception.BusinessRuleViolationException;
import ru.miit.messenger_backend.exception.ResourceNotFoundException;
import ru.miit.messenger_backend.utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Application
@Service
@Transactional
public class MessageService implements ManageMessage {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final LdapRepository ldapRepository;

    private User getUser(String uid) {
        if (ldapRepository.getUserByUid(uid).isEmpty()) throw new ResourceNotFoundException("User not found");
        Optional<User> userOptional = userRepository.getUserByUid(uid);
        if (userOptional.isEmpty()) throw new ResourceNotFoundException("User not registered");
        return userOptional.get();
    }

    private void checkUserInGroup(String uid, String ou) {
        if (ldapRepository.getAllUsersForGroup(ou)
                .stream()
                .filter(ldapUser -> ldapUser.getUid().equals(uid))
                .findAny()
                .isEmpty())
            throw new BusinessRuleViolationException("User not in group");
    }

    @Override
    public void sendMessage(String uidSender, SendMessageDto sendMessage) {
        User sender = getUser(uidSender);
        User receiver = getUser(sendMessage.receiverUid());
        if (sendMessage.groupOu() != null)
            checkUserInGroup(uidSender, sendMessage.groupOu());

        Message message = new Message(sendMessage.messageNumber(),
                AggregateReference.to(sender.getId()),
                AggregateReference.to(receiver.getId()),
                sendMessage.message(),
                sendMessage.groupOu(),
                LocalDateTime.now());
        messageRepository.save(message);

        if (sendMessage.groupOu() == null)
            simpMessagingTemplate.convertAndSendToUser(sendMessage.receiverUid(), "/queue/notification", new MessageNotificationDto(false, uidSender));
        else
            simpMessagingTemplate.convertAndSendToUser(sendMessage.receiverUid(), "/queue/notification", new MessageNotificationDto(true, message.getOuGroup()));
    }

    @Override
    public Page<ChatsDto> getChats(String uid, Pageable pageable) {
        User user = getUser(uid);

        List<ChatsServiceDto> chatsServiceDtoList = new ArrayList<>();

        for (Chat chat : messageRepository.getChats(user.getId())) {
            Optional<ChatsServiceDto> existingChat = chatsServiceDtoList.stream().filter(chatsServiceDto ->
                            ((chatsServiceDto.getReceiverId() == chat.getIdReceiver()) && (chatsServiceDto.getSenderId() == chat.getIdSender()))
                                    || ((chatsServiceDto.getReceiverId() == chat.getIdSender()) && (chatsServiceDto.getSenderId() == chat.getIdReceiver())))
                    .findAny();
            if (existingChat.isPresent()) {
                if (existingChat.get().getTimeSent().isBefore(chat.getTimeSent())) {
                    existingChat.get().setSenderId(chat.getIdSender());
                    existingChat.get().setReceiverId(chat.getIdReceiver());
                    existingChat.get().setOuGroup(chat.getOuGroup());
                    existingChat.get().setMessage(chat.getMessage());
                    existingChat.get().setTimeSent(chat.getTimeSent());
                }
            } else {
                chatsServiceDtoList.add(new ChatsServiceDto(chat.getOuGroup(), chat.getIdSender(), chat.getIdReceiver(), chat.getMessage(), chat.getTimeSent()));
            }
        }

        return Utils.paginate(chatsServiceDtoList.stream()
                .flatMap(chatsServiceDto -> {
                    if (chatsServiceDto.getOuGroup() != null) {
                        Optional<LdapGroup> ldapGroupOptional = ldapRepository.getGroupByOu(chatsServiceDto.getOuGroup());
                        if (ldapGroupOptional.isPresent()) {
                            LdapGroup ldapGroup = ldapGroupOptional.get();
                            Optional<User> senderOptional = userRepository.getUserById(chatsServiceDto.getSenderId());
                            if (senderOptional.isPresent()) {
                                User sender = senderOptional.get();
                                Optional<LdapUser> ldapSenderOptional = ldapRepository.getUserByUid(sender.getUid());
                                if (ldapSenderOptional.isPresent()) {
                                    LdapUser ldapSender = ldapSenderOptional.get();
                                    return Stream.of(new ChatsDto(true, ldapGroup.getOu(), ldapGroup.getName(), ldapSender.getName(), chatsServiceDto.getMessage(), chatsServiceDto.getTimeSent()));
                                }
                            }
                        }
                    } else {
                        int otherUserId = chatsServiceDto.getSenderId() == user.getId() ? chatsServiceDto.getReceiverId() : chatsServiceDto.getSenderId();
                        Optional<User> userOptional = userRepository.getUserById(otherUserId);
                        if (userOptional.isPresent()) {
                            User _user = userOptional.get();
                            Optional<LdapUser> ldapUserOptional = ldapRepository.getUserByUid(_user.getUid());
                            if (ldapUserOptional.isPresent()) {
                                LdapUser ldapUser = ldapUserOptional.get();
                                Optional<User> senderOptional = userRepository.getUserById(chatsServiceDto.getSenderId());
                                if (senderOptional.isPresent()) {
                                    User sender = senderOptional.get();
                                    Optional<LdapUser> ldapSenderOptional = ldapRepository.getUserByUid(sender.getUid());
                                    if (ldapSenderOptional.isPresent()) {
                                        LdapUser ldapSender = ldapSenderOptional.get();
                                        return Stream.of(new ChatsDto(false, ldapUser.getUid(), ldapUser.getName(), ldapSender.getName(), chatsServiceDto.getMessage(), chatsServiceDto.getTimeSent()));
                                    }
                                }
                            }
                        }
                    }
                    return Stream.empty();
                })
                .collect(Collectors.toList()), pageable);
    }

    @Override
    public Page<MessageDto> getOldUserMessages(String uid, String otherUserUid, Pageable pageable) {
        User user = getUser(uid);
        User otherUser = getUser(otherUserUid);

        return Utils.paginatePaginated(messageRepository.getOldUserChatMessages(user.getId(), otherUser.getId(), pageable.getPageSize(), (int) pageable.getOffset()).stream()
                        .map(message -> {
                            String senderUid = userRepository.getUserById(message.getIdSender().getId()).get().getUid();
                            return new MessageDto(message.getMessageNumber(), senderUid, message.getMessage());
                        })
                        .toList(),
                pageable,
                messageRepository.getOldUserChatMessagesSize(user.getId(), otherUser.getId()));
    }

    @Override
    public Page<MessageDto> getOldGroupMessages(String uid, String ou, Pageable pageable) {
        User user = getUser(uid);
        checkUserInGroup(uid, ou);

        return Utils.paginatePaginated(messageRepository.getOldUserGroupMessages(user.getId(), ou, pageable.getPageSize(), (int) pageable.getOffset()).stream()
                        .map(message -> {
                            String senderUid = userRepository.getUserById(message.getIdSender().getId()).get().getUid();
                            return new MessageDto(message.getMessageNumber(), senderUid, message.getMessage());
                        })
                        .toList(),
                pageable,
                messageRepository.getOldUserGroupMessagesSize(user.getId(), ou));
    }

    @Override
    public Page<MessageDto> getNewUserMessages(String uid, String otherUserUid, Pageable pageable) {
        User user = getUser(uid);
        User otherUser = getUser(otherUserUid);

        List<Message> messages = messageRepository.getNewUserChatMessages(user.getId(), otherUser.getId(), pageable.getPageSize(), (int) pageable.getOffset());

        messages.forEach(message -> {
            String senderUid = userRepository.getUserById(message.getIdSender().getId()).get().getUid();
            if (!senderUid.equals(uid) || user.getId().equals(otherUser.getId())) {
                message.receiveMessage();
                messageRepository.save(message);
                simpMessagingTemplate.convertAndSendToUser(senderUid, "/queue/received", new MessageReadNotificationDto(message.getMessageNumber()));
            }
        });

        List<MessageDto> messageDtos = messages.stream()
                .map(message -> {
                    String senderUid = userRepository.getUserById(message.getIdSender().getId()).get().getUid();
                    return new MessageDto(message.getMessageNumber(), senderUid, message.getMessage());
                })
                .toList();
        int size = messageRepository.getNewUserChatMessagesSize(user.getId(), otherUser.getId());

        return Utils.paginatePaginated(messageDtos, pageable, size);
    }

    @Override
    public Page<MessageDto> getNewGroupMessages(String uid, String ou, Pageable pageable) {
        User user = getUser(uid);
        checkUserInGroup(uid, ou);

        List<Message> messages = messageRepository.getNewUserGroupMessages(user.getId(), ou, pageable.getPageSize(), (int) pageable.getOffset());

        messages.forEach(message -> {
            message.receiveMessage();
            messageRepository.save(message);
            String senderUid = userRepository.getUserById(message.getIdSender().getId()).get().getUid();
            simpMessagingTemplate.convertAndSendToUser(senderUid, "/queue/received", new MessageReadNotificationDto(message.getMessageNumber()));
        });

        List<MessageDto> messageDtos = messages.stream()
                .map(message -> {
                    String senderUid = userRepository.getUserById(message.getIdSender().getId()).get().getUid();
                    return new MessageDto(message.getMessageNumber(), senderUid, message.getMessage());
                })
                .toList();
        int size = messageRepository.getNewUserGroupMessagesSize(user.getId(), ou);

        return Utils.paginatePaginated(messageDtos, pageable, size);
    }

    @Override
    public int newUserMessagesCount(String uid, String otherUserUid) {
        User receiver = getUser(uid);
        User sender = getUser(otherUserUid);
        return messageRepository.getNewUserChatMessagesSize(receiver.getId(), sender.getId());
    }

    @Override
    public int newGroupMessagesCount(String uid, String ou) {
        User receiver = getUser(uid);
        checkUserInGroup(uid, ou);
        return messageRepository.getNewUserGroupMessagesSize(receiver.getId(), ou);
    }

    @Data
    @AllArgsConstructor
    private static class ChatsServiceDto {
        private String ouGroup;
        private int senderId;
        private int receiverId;
        private byte[] message;
        private LocalDateTime timeSent;
    }
}
