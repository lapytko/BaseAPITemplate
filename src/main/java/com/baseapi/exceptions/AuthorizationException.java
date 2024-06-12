package com.baseapi.exceptions;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException() {
        super("Invalid token");
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Exception ex) {
        super(ex.getMessage());
    }
}