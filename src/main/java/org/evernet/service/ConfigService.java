package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.Vertex;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Config;
import org.evernet.repository.ConfigRepository;
import org.evernet.util.Random;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    private static final String JWT_SIGNING_KEY = "jwtSigningKey";
    private static final String VERTEX_ENDPOINT = "vertexEndpoint";
    private static final String VERTEX_DISPLAY_NAME = "vertexDisplayName";
    private static final String VERTEX_DESCRIPTION = "vertexDescription";

    public void init(Vertex vertex) {
        insert(VERTEX_ENDPOINT, vertex.getEndpoint());
        insert(VERTEX_DISPLAY_NAME, vertex.getDisplayName());
        insert(VERTEX_DESCRIPTION, vertex.getDescription());
        insert(JWT_SIGNING_KEY, Random.generateRandomString(128));
    }

    public void insert(String key, String value) {
        configRepository.insertIfNotExists(UUID.randomUUID().toString(), key, value);
    }

    public void set(String key, String value) {
        configRepository.upsert(UUID.randomUUID().toString(), key, value);
    }

    public Config get(String key) {
        Config config = configRepository.findByKey(key);

        if (config == null) {
            throw new NotFoundException(String.format("Config %s not found", key));
        }

        return config;
    }

    public String getJwtSigningKey() {
        return get(JWT_SIGNING_KEY).getValue();
    }

    public String getVertexEndpoint() {
        return get(VERTEX_ENDPOINT).getValue();
    }

    public String getVertexDisplayName() {
        return get(VERTEX_DISPLAY_NAME).getValue();
    }

    public String getVertexDescription() {
        return get(VERTEX_DESCRIPTION).getValue();
    }
}
