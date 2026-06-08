package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigReaderService {

    private final ConfigService configService;

    public String getVertexEndpoint() {
        return configService.get(ConfigService.VERTEX_ENDPOINT, "localhost:3000");
    }

    public String getVertexDisplayName() {
        return configService.get(ConfigService.VERTEX_DISPLAY_NAME, "Vertex");
    }

    public String getVertexDescription() {
        return configService.get(ConfigService.VERTEX_DESCRIPTION, "Vertex");
    }

    public String getJwtSigningKey() {
        return configService.get(ConfigService.JWT_SIGNING_KEY, "secret");
    }

    public String getSigningPublicKey() {
        return configService.get(ConfigService.SIGNING_PUBLIC_KEY, "secret");
    }
}
