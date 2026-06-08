package xyz.evernet.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.auth.*;
import xyz.evernet.exception.InvalidTokenException;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class VertexAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Override
    public void authenticate(HttpServletRequest request) {
        String jwtToken = AuthUtils.extractToken(request);

        AuthenticatedVertex vertex = jwt.getVertex(jwtToken);

        if (Objects.isNull(vertex)) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setVertex(vertex);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedVertexController.class;
    }
}