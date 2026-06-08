package xyz.evernet.auth;

public class ThreadLocalWrapper {

    private static final ThreadLocal<AuthenticatedUser> userContext;

    private static final ThreadLocal<AuthenticatedAdmin> adminContext;

    private static final ThreadLocal<AuthenticatedVertex> vertexContext;

    static {
        userContext = new ThreadLocal<>();
        adminContext = new ThreadLocal<>();
        vertexContext = new ThreadLocal<>();
    }

    public static AuthenticatedUser getUser() {
        return userContext.get();
    }

    public static void setUser(AuthenticatedUser user) {
        userContext.set(user);
    }

    public static AuthenticatedAdmin getAdmin() {
        return adminContext.get();
    }

    public static void setAdmin(AuthenticatedAdmin admin) {
        adminContext.set(admin);
    }

    public static AuthenticatedVertex getVertex() {
        return vertexContext.get();
    }

    public static void setVertex(AuthenticatedVertex vertex) {
        vertexContext.set(vertex);
    }
}