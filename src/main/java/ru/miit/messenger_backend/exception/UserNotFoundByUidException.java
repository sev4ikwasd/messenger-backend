package ru.miit.messenger_backend.exception;

public class UserNotFoundByUidException extends NotFoundException {
    public UserNotFoundByUidException(String uid) {
        super("User with uid: " + uid + " was not found");
    }
}
