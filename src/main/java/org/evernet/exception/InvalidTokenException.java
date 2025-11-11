package org.evernet.exception;

public class InvalidTokenException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Invalid access token";
    }
}