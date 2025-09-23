package org.evernet.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.evernet.bean.ActorAddress;
import org.evernet.bean.NodeAddress;
import org.evernet.exception.InvalidTokenException;
import org.evernet.service.ConfigService;
import org.evernet.service.NodeKeyService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";
    private static final String TOKEN_TYPE_ACTOR = "ACTOR";

    private final ConfigService configService;
    private final NodeKeyService nodeKeyService;

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

    public AuthenticatedActor getActor(String token) {
        Jws<Claims> jwsClaims = Jwts.parser()
                .keyLocator(new LocatorAdapter<>() {
                    @Override
                    protected Key locate(JwsHeader header) {
                        try {
                            return nodeKeyService.getPublicKey(header.getKeyId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACTOR)
                .build()
                .parseSignedClaims(token);

        String kid = jwsClaims.getHeader().getKeyId();
        if (StringUtils.isBlank(kid)) {
            throw new InvalidTokenException();
        }

        Optional<String> audience = jwsClaims.getPayload().getAudience().stream().findFirst();
        if (audience.isEmpty()) {
            throw new InvalidTokenException();
        }

        String issuer = jwsClaims.getPayload().getIssuer();

        if (StringUtils.isBlank(issuer)) {
            throw new InvalidTokenException();
        }

        if (!issuer.equals(kid)) {
            throw new InvalidTokenException();
        }

        NodeAddress targetNodeAddress = NodeAddress.fromString(audience.get());

        if (!targetNodeAddress.getVertexEndpoint().equals(configService.getVertexEndpoint())) {
            throw new InvalidTokenException();
        }

        return AuthenticatedActor.builder()
                .address(ActorAddress.builder()
                        .identifier(jwsClaims.getPayload().getSubject())
                        .nodeAddress(NodeAddress.fromString(issuer))
                        .build())
                .targetNodeAddress(targetNodeAddress)
                .build();
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