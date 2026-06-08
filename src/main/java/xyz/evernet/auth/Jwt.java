package xyz.evernet.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.exception.InvalidTokenException;
import xyz.evernet.service.ConfigReaderService;
import xyz.evernet.service.NodeKeyService;
import xyz.evernet.util.Ed25519KeyHelper;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_USER = "USER";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";
    private static final String TOKEN_TYPE_VERTEX = "VERTEX";

    private final ConfigReaderService configReaderService;

    private final NodeKeyService nodeKeyService;

    public AuthenticatedUser getUser(String token) {
        String vertexEndpoint = configReaderService.getVertexEndpoint();
        String jwtSigningKey = configReaderService.getJwtSigningKey();

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSigningKey.getBytes(StandardCharsets.UTF_8)))
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_USER)
                .requireAudience(vertexEndpoint)
                .requireIssuer(vertexEndpoint)
                .build()
                .parseSignedClaims(token).getPayload();

        return AuthenticatedUser.builder()
                .username(claims.getSubject())
                .build();
    }

    public String getUserToken(AuthenticatedUser user) {
        String vertexEndpoint = configReaderService.getVertexEndpoint();
        String jwtSigningKey = configReaderService.getJwtSigningKey();

        return Jwts.builder().subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L))
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_USER)
                .issuer(vertexEndpoint)
                .audience().add(vertexEndpoint)
                .and()
                .signWith(Keys.hmacShaKeyFor(jwtSigningKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public AuthenticatedAdmin getAdmin(String token) {
        String vertexEndpoint = configReaderService.getVertexEndpoint();
        String jwtSigningKey = configReaderService.getJwtSigningKey();

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSigningKey.getBytes(StandardCharsets.UTF_8)))
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
        String vertexEndpoint = configReaderService.getVertexEndpoint();
        String jwtSigningKey = configReaderService.getJwtSigningKey();

        return Jwts.builder().subject(admin.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000L))
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ADMIN)
                .issuer(vertexEndpoint)
                .audience().add(vertexEndpoint)
                .and()
                .signWith(Keys.hmacShaKeyFor(jwtSigningKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String getVertexToken(String targetVertexEndpoint) throws Exception {
        String currentVertexEndpoint = configReaderService.getVertexEndpoint();
        PrivateKey jwtSigningKey = Ed25519KeyHelper.stringToPrivateKey(configReaderService.getSigningPrivateKey());

        return Jwts.builder().subject(currentVertexEndpoint)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 1000L))
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_VERTEX)
                .issuer(currentVertexEndpoint)
                .audience().add(targetVertexEndpoint)
                .and()
                .header().keyId(currentVertexEndpoint)
                .and()
                .signWith(jwtSigningKey)
                .compact();
    }

    public AuthenticatedVertex getVertex(String token)  {
        String currentVertexEndpoint = configReaderService.getVertexEndpoint();

        Jws<Claims> claimsJws = Jwts.parser()
                .keyLocator(header -> {
                    Object keyIdObj = header.get("kid");
                    if (keyIdObj == null)  {
                        throw new InvalidTokenException();
                    }
                    try {
                        return nodeKeyService.getSigningPublicKey(keyIdObj.toString());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .require(TOKEN_TYPE_CLAIM, TOKEN_TYPE_VERTEX)
                .build()
                .parseSignedClaims(token);

        Claims claims = claimsJws.getPayload();
        String keyId = claimsJws.getHeader().getKeyId();
        String issuer = claims.getIssuer();
        String subject = claims.getSubject();

        if (!keyId.equals(issuer)) {
            throw new InvalidTokenException();
        }

        if (!issuer.equals(subject)) {
            throw new InvalidTokenException();
        }

        if (claims.getAudience().stream().findFirst().isEmpty()) {
            throw new InvalidTokenException();
        }

        if (!currentVertexEndpoint.equals(claims.getAudience().stream().findFirst().get())) {
            throw new InvalidTokenException();
        }

        return AuthenticatedVertex.builder()
                .vertexEndpoint(subject)
                .build();
    }
}
