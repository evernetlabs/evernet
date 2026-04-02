package xyz.evernet.auth;

public class ThreadLocalWrapper {

    private static final ThreadLocal<AuthenticatedAdmin> adminContext;
    private static final ThreadLocal<AuthenticatedUser> userContext;

    static {
        adminContext = new ThreadLocal<>();
        userContext = new ThreadLocal<>();
    }

    public static AuthenticatedAdmin getAdmin() {
        return adminContext.get();
    }

    public static void setAdmin(AuthenticatedAdmin admin) {
        adminContext.set(admin);
    }

    public static AuthenticatedUser getUser() {
        return userContext.get();
    }

    public static void setUser(AuthenticatedUser user) {
        userContext.set(user);
    }
}