package xyz.evernet.exception;

public class AuthenticationException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Invalid username and password combination";
    }
}