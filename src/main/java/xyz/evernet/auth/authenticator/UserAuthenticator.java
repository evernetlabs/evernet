package xyz.evernet.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.auth.AuthenticatedUser;
import xyz.evernet.auth.AuthenticatedUserController;
import xyz.evernet.auth.Jwt;
import xyz.evernet.auth.ThreadLocalWrapper;
import xyz.evernet.exception.InvalidTokenException;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Override
    public void authenticate(HttpServletRequest request) {
        String jwtToken = AuthUtils.extractToken(request);

        AuthenticatedUser user = jwt.getUser(jwtToken);

        if (Objects.isNull(user)) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setUser(user);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedUserController.class;
    }
}