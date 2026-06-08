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

    public String getJwtSigningKey() {
        return configService.get(ConfigService.JWT_SIGNING_KEY, "secret");
    }
}
