package xyz.evernet.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String ORGANIZATION_ID_CLAIM = "organizationId";
    private static final String ADMIN_CLAIM = "admin";
    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_USER = "USER";
    private static final String ISSUER = "evernet";
    private static final String AUDIENCE = "evernet";

    @Value("${evernet.jwt.secret.key}")
    private String jwtSecretKey;

    public AuthenticatedUser getUser(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)))
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_USER)
                .requireAudience(AUDIENCE)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token).getPayload();

        return AuthenticatedUser.builder()
                .id(claims.getSubject())
                .organizationId((String) claims.getOrDefault("organizationId", ""))
                .admin((Boolean) claims.getOrDefault("admin", false))
                .build();
    }

    public String getUserToken(AuthenticatedUser user) {
        return Jwts.builder().subject(user.getId())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_USER)
                .claim(ORGANIZATION_ID_CLAIM, user.getOrganizationId())
                .claim(ADMIN_CLAIM, user.getAdmin())
                .issuer(ISSUER)
                .audience().add(AUDIENCE)
                .and()
                .signWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}