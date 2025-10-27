package org.evernet.auth;

import org.evernet.exception.NotAllowedException;

public class AuthenticatedActorController {

    public AuthenticatedActor getActor() {
        return ThreadLocalWrapper.getActor();
    }

    public String getTargetNodeIdentifier() {
        return getActor().getTargetNodeAddress().getIdentifier();
    }

    public String getActorIdentifier() {
        return getActor().getAddress().getIdentifier();
    }

    public String getActorNodeIdentifier() {
        return getActor().getAddress().getNodeAddress().getIdentifier();
    }

    public String getActorAddress() {
        return getActor().getAddress().toString();
    }

    public Boolean isLocal() {
        AuthenticatedActor actor = getActor();
        return actor.getTargetNodeAddress().equals(actor.getAddress().getNodeAddress());
    }

    public void checkLocal() {
        if (!isLocal()) {
            throw new NotAllowedException();
        }
    }
}