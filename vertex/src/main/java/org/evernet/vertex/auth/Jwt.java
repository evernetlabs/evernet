package org.evernet.vertex.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.evernet.vertex.service.ConfigService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";

    private final ConfigService configService;

    public AuthenticatedAdmin getAdmin(String token) {
        String vertexEndpoint = configService.getVertexEndpoint();

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(configService.getJwtSigningKey().getBytes(StandardCharsets.UTF_8)))
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .requireAudience(vertexEndpoint)
                .requireIssuer(vertexEndpoint)
                .build()
                .parseSignedClaims(token).getPayload();

        return AuthenticatedAdmin.builder()
                .identifier(claims.getSubject())
                .build();
    }

    public String getAdminToken(AuthenticatedAdmin admin) {
        String vertexEndpoint = configService.getVertexEndpoint();

        return Jwts.builder().subject(admin.getIdentifier())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .issuer(vertexEndpoint)
                .audience().add(vertexEndpoint)
                .and()
                .signWith(Keys.hmacShaKeyFor(configService.getJwtSigningKey().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}