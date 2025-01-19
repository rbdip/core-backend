package ru.stepagin.backend.exception;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}
