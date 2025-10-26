package org.evernet.exception;

public class AuthenticationException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Invalid identifier and password combination";
    }
}