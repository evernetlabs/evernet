package xyz.evernet.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.service.VertexConfigService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";

    private final VertexConfigService vertexConfigService;

    public AuthenticatedAdmin getAdmin(String token) {
        String vertexEndpoint = vertexConfigService.getVertexEndpoint();

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(vertexConfigService.getJwtSigningKey().getBytes(StandardCharsets.UTF_8)))
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .requireAudience(vertexEndpoint)
                .requireIssuer(vertexEndpoint)
                .build()
                .parseSignedClaims(token).getPayload();

        return AuthenticatedAdmin.builder()
                .username(claims.getSubject())
                .build();
    }

    public String getAdminToken(AuthenticatedAdmin admin) {
        String vertexEndpoint = vertexConfigService.getVertexEndpoint();

        return Jwts.builder().subject(admin.getUsername())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .issuer(vertexEndpoint)
                .issuedAt(new Date())
                .expiration(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .audience().add(vertexEndpoint)
                .and()
                .signWith(Keys.hmacShaKeyFor(vertexConfigService.getJwtSigningKey().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}