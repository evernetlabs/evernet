package org.evernet.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.auth.Jwt;
import org.evernet.auth.ThreadLocalWrapper;
import org.evernet.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ActorAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Override
    public void authenticate(HttpServletRequest request) {
        String jwtToken = AuthUtils.extractToken(request);

        AuthenticatedActor actor = jwt.getActor(jwtToken);

        if (Objects.isNull(actor)) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setActor(actor);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedActorController.class;
    }
}