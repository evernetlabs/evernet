package org.evernet.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.evernet.service.ConfigService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";
    private static final String TOKEN_TYPE_ACTOR = "ACTOR";

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

    public String getActorToken(AuthenticatedActor actor, PrivateKey privateKey) {
        return Jwts.builder().subject(actor.getAddress().getIdentifier())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACTOR)
                .issuer(actor.getAddress().getNodeAddress().toString())
                .audience().add(actor.getTargetNodeAddress().toString())
                .and()
                .header().keyId(actor.getAddress().getNodeAddress().toString())
                .and()
                .signWith(privateKey)
                .compact();
    }
}