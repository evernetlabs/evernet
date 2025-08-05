package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Config;
import org.evernet.repository.ConfigRepository;
import org.evernet.util.Random;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    public void init(String vertexEndpoint, String vertexDisplayName, String vertexDescription) {
        List<Config> configs = List.of(
                Config.builder().key("VERTEX_ENDPOINT").value(vertexEndpoint).build(),
                Config.builder().key("VERTEX_DISPLAY_NAME").value(vertexDisplayName).build(),
                Config.builder().key("VERTEX_DESCRIPTION").value(vertexDescription).build(),
                Config.builder().key("JWT_SIGNING_KEY").value(Random.generateRandomString(128)).build()
        );

        configRepository.saveAll(configs);
    }

    public String get(String key, String defaultValue) {
        Config config = configRepository.findByKey(key);

        if (config == null) {
            return defaultValue;
        }

        return config.getValue();
    }

    public void set(String key, String value) {
        Config config = configRepository.findByKey(key);
        if (config == null) {
            config = Config.builder().key(key).value(value).build();
        }
        config.setValue(value);
        configRepository.save(config);
    }

    public String getVertexDisplayName() {
        return get("VERTEX_DISPLAY_NAME", "vertex");
    }

    public String getVertexEndpoint() {
        return get("VERTEX_ENDPOINT", "localhost:5000");
    }

    public String getVertexDescription() {
        return get("VERTEX_DESCRIPTION", "vertex");
    }

    public String getJwtSigningKey() {
        return get("JWT_SIGNING_KEY", "secret.secret.secret.secret.secret.secret.secret.secret.secret.secret.secret.secret");
    }

    public String getFederationProtocol() {
        return  get("FEDERATION_PROTOCOL", "http");
    }
}
