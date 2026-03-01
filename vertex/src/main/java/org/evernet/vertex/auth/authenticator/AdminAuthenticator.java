package org.evernet.vertex.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.evernet.vertex.auth.AuthenticatedAdmin;
import org.evernet.vertex.auth.AuthenticatedAdminController;
import org.evernet.vertex.auth.Jwt;
import org.evernet.vertex.auth.ThreadLocalWrapper;
import org.evernet.vertex.exception.InvalidTokenException;
import org.springframework.stereotype.Component;

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