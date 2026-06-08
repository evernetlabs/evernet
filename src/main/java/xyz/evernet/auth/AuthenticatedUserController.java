package xyz.evernet.auth;

import xyz.evernet.exception.NotAllowedException;

public class AuthenticatedUserController {

    public AuthenticatedUser getUser() {
        return ThreadLocalWrapper.getUser();
    }

    public String getUserId() {
        return getUser().getId();
    }

    public String getOrganizationId() {
        return getUser().getOrganizationId();
    }

    public Boolean isAdmin() {
        return getUser().getAdmin();
    }

    public void checkAdmin() {
        if (!isAdmin()) {
            throw new NotAllowedException();
        }
    }
}