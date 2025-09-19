package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Config;
import org.evernet.repository.ConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    private static final String KEY_VERTEX_ENDPOINT = "vertexEndpoint";
    private static final String KEY_VERTEX_DISPLAY_NAME = "vertexDisplayName";
    private static final String KEY_VERTEX_DESCRIPTION = "vertexDescription";
    private static final String KEY_FEDERATION_PROTOCOL = "federationProtocol";
    private static final String KEY_JWT_SIGNING_KEY = "jwtSigningKey";

    public void init(String vertexEndpoint, String vertexDisplayName, String vertexDescription, String federationProtocol, String jwtSigningKey) {
        List<Config> configs = List.of(
                Config.builder().key(KEY_VERTEX_ENDPOINT).value(vertexEndpoint).build(),
                Config.builder().key(KEY_VERTEX_DISPLAY_NAME).value(vertexDisplayName).build(),
                Config.builder().key(KEY_VERTEX_DESCRIPTION).value(vertexDescription).build(),
                Config.builder().key(KEY_FEDERATION_PROTOCOL).value(federationProtocol).build(),
                Config.builder().key(KEY_JWT_SIGNING_KEY).value(jwtSigningKey).build()
        );

        configRepository.saveAll(configs);
    }

    public Config get(String key, String defaultValue) {
        Config config = configRepository.findByKey(key);

        if (config == null) {
            return Config.builder().key(key).value(defaultValue).build();
        }

        return config;
    }

    public String getVertexEndpoint() {
        return get(KEY_VERTEX_ENDPOINT, "localhost:5000").getValue();
    }

    public String getVertexDisplayName() {
        return get(KEY_VERTEX_DISPLAY_NAME, "vertex").getValue();
    }

    public String getVertexDescription() {
        return get(KEY_VERTEX_DESCRIPTION, "Vertex").getValue();
    }

    public String getJwtSigningKey() {
        return get(KEY_JWT_SIGNING_KEY, "secret").getValue();
    }

    public String getFederationProtocol() {
        return get(KEY_FEDERATION_PROTOCOL, "http").getValue();
    }
}
