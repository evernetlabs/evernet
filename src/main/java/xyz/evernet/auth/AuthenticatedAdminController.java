package xyz.evernet.auth;

public class AuthenticatedAdminController {

    public AuthenticatedUser getUser() {
        return ThreadLocalWrapper.getUser();
    }

    public String getUsername() {
        return getUser().getUsername();
    }
}