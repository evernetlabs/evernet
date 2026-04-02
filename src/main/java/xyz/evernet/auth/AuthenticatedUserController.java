package xyz.evernet.auth;

import xyz.evernet.exception.NotAllowedException;

public class AuthenticatedUserController {

    public AuthenticatedUser getUser() {
        return ThreadLocalWrapper.getUser();
    }

    public String getTargetNodeIdentifier() {
        return getUser().getTargetNodeAddress().getIdentifier();
    }

    public String getUsername() {
        return getUser().getAddress().getUsername();
    }

    public String getUserNodeIdentifier() {
        return getUser().getAddress().getNodeAddress().getIdentifier();
    }

    public String getUserAddress() {
        return getUser().getAddress().toString();
    }

    public Boolean isLocal() {
        AuthenticatedUser user = getUser();
        return user.getTargetNodeAddress().equals(user.getAddress().getNodeAddress());
    }

    public void checkLocal() {
        if (!isLocal()) {
            throw new NotAllowedException();
        }
    }
}