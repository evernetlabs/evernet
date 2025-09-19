package org.evernet.auth;

public class ThreadLocalWrapper {

    private static final ThreadLocal<AuthenticatedAdmin> adminContext;

    private static final ThreadLocal<AuthenticatedActor> actorContext;

    static {
        adminContext = new ThreadLocal<>();
        actorContext = new ThreadLocal<>();
    }

    public static AuthenticatedAdmin getAdmin() {
        return adminContext.get();
    }

    public static void setAdmin(AuthenticatedAdmin admin) {
        adminContext.set(admin);
    }

    public static AuthenticatedActor getActor() {
        return actorContext.get();
    }

    public static void setActor(AuthenticatedActor actor) {
        actorContext.set(actor);
    }
}