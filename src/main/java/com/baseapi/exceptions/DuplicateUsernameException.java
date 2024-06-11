package com.baseapi.exceptions;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException() {
        super("Username already exists");
    }

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
