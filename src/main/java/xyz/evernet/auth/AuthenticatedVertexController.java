package xyz.evernet.auth;

public class AuthenticatedVertexController {

    public AuthenticatedVertex getVertex() {
        return ThreadLocalWrapper.getVertex();
    }

    public String getSourceVertexEndpoint() {
        return getVertex().getVertexEndpoint();
    }
}