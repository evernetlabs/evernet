package org.evernet.vertex.auth.authenticator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.evernet.vertex.exception.InvalidTokenException;

import java.util.Objects;

@UtilityClass
public class AuthUtils {


    public static String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (Objects.isNull(authorizationHeader)) {
            throw new InvalidTokenException();
        }

        String[] authorizationComponents = authorizationHeader.split("\\s+");

        if (authorizationComponents.length != 2) {
            throw new InvalidTokenException();
        }

        if (!authorizationComponents[0].equals("Bearer")) {
            throw new InvalidTokenException();
        }

        return authorizationComponents[1];
    }
}