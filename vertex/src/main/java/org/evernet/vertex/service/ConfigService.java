package org.evernet.vertex.service;

import lombok.RequiredArgsConstructor;
import org.evernet.vertex.bean.Vertex;
import org.evernet.vertex.enums.FederationProtocol;
import org.evernet.vertex.model.Config;
import org.evernet.vertex.repository.ConfigRepository;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigRepository configRepository;

    private final MongoTemplate mongoTemplate;

    private static final String KEY_VERTEX_ENDPOINT = "vertexEndpoint";
    private static final String KEY_VERTEX_DISPLAY_NAME = "vertexDisplayName";
    private static final String KEY_VERTEX_DESCRIPTION = "vertexDescription";
    private static final String KEY_JWT_SIGNING_KEY = "jwtSigningKey";
    private static final String KEY_FEDERATION_PROTOCOL = "federationProtocol";

    public void setIfAbsent(String key, String value) {
        Update update = new Update();
        update.setOnInsert("key", key);
        update.setOnInsert("value", value);
        update.setOnInsert("createdAt", Instant.now());
        update.setOnInsert("updatedAt", Instant.now());

        Query query = Query.query(Criteria.where("key").is(key));

        mongoTemplate.upsert(query, update, Config.class);
    }

    public Object get(String key, Object defaultValue) {
        Config config = configRepository.findByKey(key);

        if (config == null) {
            return defaultValue;
        }

        return config.getValue();
    }

    public Config set(String key, String value) {
        Update update = new Update();

        update.setOnInsert("key", key);
        update.set("value", value);
        update.setOnInsert("createdAt", Instant.now());
        update.set("updatedAt", Instant.now());

        Query query = Query.query(Criteria.where("key").is(key));

        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true).upsert(true), Config.class);
    }

    public void init(Vertex vertex, String jwtSigningKey, FederationProtocol federationProtocol) {
        setIfAbsent(KEY_VERTEX_ENDPOINT, vertex.getEndpoint());
        setIfAbsent(KEY_VERTEX_DISPLAY_NAME, vertex.getDisplayName());
        setIfAbsent(KEY_VERTEX_DESCRIPTION, vertex.getDescription());
        setIfAbsent(KEY_JWT_SIGNING_KEY, jwtSigningKey);
        setIfAbsent(KEY_FEDERATION_PROTOCOL, federationProtocol.name());
    }

    public Vertex getVertex() {
        String endpoint = (String) get(KEY_VERTEX_ENDPOINT, "localhost:5000");
        String displayName = (String) get(KEY_VERTEX_DISPLAY_NAME, "Evernet Vertex");
        String description = (String) get(KEY_VERTEX_DESCRIPTION, "An Evernet Vertex");

        return Vertex.builder()
                .endpoint(endpoint)
                .displayName(displayName)
                .description(description)
                .build();
    }

    public String getVertexEndpoint() {
        return (String) get(KEY_VERTEX_ENDPOINT, "localhost:5000");
    }

    public String getJwtSigningKey() {
        return (String) get(KEY_JWT_SIGNING_KEY, "default-signing-key");
    }

    public FederationProtocol getFederationProtocol() {
        String protocolStr = (String) get(KEY_FEDERATION_PROTOCOL, FederationProtocol.HTTP.name());
        return FederationProtocol.valueOf(protocolStr);
    }

    public Config setFederationProtocol(FederationProtocol federationProtocol) {
        return set(KEY_FEDERATION_PROTOCOL, federationProtocol.name());
    }

    public Vertex setVertex(Vertex vertex) {
        set(KEY_VERTEX_ENDPOINT, vertex.getEndpoint());
        set(KEY_VERTEX_DISPLAY_NAME, vertex.getDisplayName());
        set(KEY_VERTEX_DESCRIPTION, vertex.getDescription());
        return vertex;
    }

    public void resetJwtSigningKey(String jwtSigningKey) {
        set(KEY_JWT_SIGNING_KEY, jwtSigningKey);
    }
}
