package org.evernet.vertex.auth;

public class ThreadLocalWrapper {

    private static final ThreadLocal<AuthenticatedAdmin> adminContext;

    static {
        adminContext = new ThreadLocal<>();
    }

    public static AuthenticatedAdmin getAdmin() {
        return adminContext.get();
    }

    public static void setAdmin(AuthenticatedAdmin admin) {
        adminContext.set(admin);
    }
}