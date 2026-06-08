package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import xyz.evernet.bean.Vertex;
import xyz.evernet.model.Config;
import xyz.evernet.repository.ConfigRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigService {

    static final String JWT_SIGNING_KEY = "jwtSigningKey";
    static final String VERTEX_ENDPOINT = "vertexEndpoint";
    static final String VERTEX_DISPLAY_NAME = "vertexDisplayName";
    static final String VERTEX_DESCRIPTION = "vertexDescription";
    static final String FEDERATION_PROTOCOL = "federationProtocol";
    static final String SIGNING_PRIVATE_KEY = "signingPrivateKey";
    static final String SIGNING_PUBLIC_KEY = "signingPublicKey";

    private final ConfigRepository configRepository;

    public void init(Vertex vertex, String federationProtocol, String jwtSigningKey, String signingPrivateKey, String signingPublicKey) {
        List<Config> configs = List.of(
                Config.builder().key(JWT_SIGNING_KEY).value(jwtSigningKey).build(),
                Config.builder().key(VERTEX_ENDPOINT).value(vertex.getEndpoint()).build(),
                Config.builder().key(VERTEX_DISPLAY_NAME).value(vertex.getDisplayName()).build(),
                Config.builder().key(VERTEX_DESCRIPTION).value(vertex.getDescription()).build(),
                Config.builder().key(FEDERATION_PROTOCOL).value(federationProtocol).build(),
                Config.builder().key(SIGNING_PRIVATE_KEY).value(signingPrivateKey).build(),
                Config.builder().key(SIGNING_PUBLIC_KEY).value(signingPublicKey).build()
        );

        configRepository.saveAll(configs);
    }

    public void setVertex(Vertex vertex) {
        set(VERTEX_ENDPOINT, vertex.getEndpoint());
        set(VERTEX_DISPLAY_NAME, vertex.getDisplayName());
        set(VERTEX_DESCRIPTION, vertex.getDescription());
    }

    public void setJwtSigningKey(String jwtSigningKey) {
        set(JWT_SIGNING_KEY, jwtSigningKey);
    }

    public void setFederationProtocol(String federationProtocol) {
        set(FEDERATION_PROTOCOL, federationProtocol);
    }

    public void setSigningKeys(String signingPrivateKey, String signingPublicKey) {
        set(SIGNING_PRIVATE_KEY, signingPrivateKey);
        set(SIGNING_PUBLIC_KEY, signingPublicKey);
    }

    public void set(String key, String value) {
        Config config = configRepository.findByKey(key);
        if (config != null) {
            config.setValue(value);
            configRepository.save(config);
        }
    }

    @Cacheable("configCache")
    public String get(String key, String defaultValue) {
        Config config = configRepository.findByKey(key);

        if (config == null) {
            return defaultValue;
        }

        return config.getValue();
    }
}
