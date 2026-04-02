package xyz.evernet.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xyz.evernet.bean.NodeAddress;
import xyz.evernet.bean.UserAddress;
import xyz.evernet.exception.InvalidTokenException;
import xyz.evernet.service.NodeKeyService;
import xyz.evernet.service.VertexConfigService;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";
    private static final String TOKEN_TYPE_USER = "USER";

    private final VertexConfigService vertexConfigService;

    private final NodeKeyService nodeKeyService;

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

    public AuthenticatedUser getUser(String token) {
        Jws<Claims> jwsClaims = Jwts.parser()
                .keyLocator(new LocatorAdapter<Key>() {
                    @Override
                    protected Key locate(JwsHeader header) {
                        try {
                            return nodeKeyService.getPublicKey(header.getKeyId());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_USER)
                .build()
                .parseSignedClaims(token);

        String kid = jwsClaims.getHeader().getKeyId();
        if (!StringUtils.hasText(kid)) {
            throw new InvalidTokenException();
        }

        Optional<String> audience = jwsClaims.getPayload().getAudience().stream().findFirst();
        if (audience.isEmpty()) {
            throw new InvalidTokenException();
        }

        String issuer = jwsClaims.getPayload().getIssuer();

        if (!StringUtils.hasText(issuer)) {
            throw new InvalidTokenException();
        }

        if (!issuer.equals(kid)) {
            throw new InvalidTokenException();
        }

        NodeAddress targetNodeAddress = NodeAddress.fromString(audience.get());

        if (!targetNodeAddress.getVertexEndpoint().equals(vertexConfigService.getVertexEndpoint())) {
            throw new InvalidTokenException();
        }

        return AuthenticatedUser.builder()
                .address(UserAddress.builder()
                        .username(jwsClaims.getPayload().getSubject())
                        .nodeAddress(NodeAddress.fromString(issuer))
                        .build())
                .targetNodeAddress(targetNodeAddress)
                .build();
    }

    public String getUserToken(AuthenticatedUser user, PrivateKey privateKey) {
        return Jwts.builder().subject(user.getAddress().getUsername())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_USER)
                .issuer(user.getAddress().getNodeAddress().toString())
                .expiration(new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli()))
                .issuedAt(new Date())
                .audience().add(user.getTargetNodeAddress().toString())
                .and()
                .header().keyId(user.getAddress().getNodeAddress().toString())
                .and()
                .signWith(privateKey)
                .compact();
    }
}