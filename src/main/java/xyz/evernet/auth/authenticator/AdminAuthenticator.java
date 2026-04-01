package xyz.evernet.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.auth.AuthenticatedAdmin;
import xyz.evernet.auth.AuthenticatedAdminController;
import xyz.evernet.auth.Jwt;
import xyz.evernet.auth.ThreadLocalWrapper;
import xyz.evernet.exception.InvalidTokenException;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AdminAuthenticator implements Authenticator {

    private final Jwt jwt;

    @Override
    public void authenticate(HttpServletRequest request) {
        String jwtToken = AuthUtils.extractToken(request);

        AuthenticatedAdmin admin = jwt.getAdmin(jwtToken);

        if (Objects.isNull(admin)) {
            throw new InvalidTokenException();
        }

        ThreadLocalWrapper.setAdmin(admin);
    }

    @Override
    public Class<?> getType() {
        return AuthenticatedAdminController.class;
    }
}