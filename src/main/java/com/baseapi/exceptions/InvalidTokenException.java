package com.baseapi.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid token");
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(Exception ex) {
        super(ex.getMessage());
    }
}