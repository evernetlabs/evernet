package xyz.evernet.auth;

public class ThreadLocalWrapper {

    private static final ThreadLocal<AuthenticatedUser> userContext;

    static {
        userContext = new ThreadLocal<>();
    }

    public static AuthenticatedUser getUser() {
        return userContext.get();
    }

    public static void setUser(AuthenticatedUser user) {
        userContext.set(user);
    }
}