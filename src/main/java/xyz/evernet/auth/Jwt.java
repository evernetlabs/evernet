package xyz.evernet.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.service.ConfigReaderService;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Jwt {

    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String TOKEN_TYPE_USER = "USER";
    private static final String TOKEN_TYPE_ADMIN = "ADMIN";

    private final ConfigReaderService configReaderService;

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
}