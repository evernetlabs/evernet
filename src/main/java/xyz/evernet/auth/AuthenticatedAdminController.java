package xyz.evernet.auth;

public class AuthenticatedAdminController {

    public AuthenticatedUser getAdmin() {
        return ThreadLocalWrapper.getUser();
    }

    public String getUsername() {
        return getAdmin().getUsername();
    }
}