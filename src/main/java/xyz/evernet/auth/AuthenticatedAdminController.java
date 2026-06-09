package xyz.evernet.auth;

public class AuthenticatedAdminController {

    public AuthenticatedAdmin getAdmin() {
        return ThreadLocalWrapper.getAdmin();
    }

    public String getUsername() {
        return getAdmin().getUsername();
    }
}