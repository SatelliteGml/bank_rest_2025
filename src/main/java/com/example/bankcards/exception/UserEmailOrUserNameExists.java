package com.example.bankcards.exception;

public class UserEmailOrUserNameExists extends RuntimeException {
    public UserEmailOrUserNameExists(String message) {
        super(message);
    }
}
