package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.evernet.enums.FederationProtocol;
import xyz.evernet.model.Vertex;
import xyz.evernet.response.VertexFederationProtocolResponse;
import xyz.evernet.util.Random;

@Service
@RequiredArgsConstructor
public class VertexConfigService {

    private static final String KEY_VERTEX_ENDPOINT = "vertexEndpoint";
    private static final String KEY_VERTEX_DISPLAY_NAME = "vertexDisplayName";
    private static final String KEY_VERTEX_DESCRIPTION = "vertexDescription";
    private static final String KEY_FEDERATION_PROTOCOL = "federationProtocol";
    private static final String KEY_JWT_SIGNING_KEY = "jwtSigningKey";
    private final ConfigService configService;

    public void init(Vertex vertex) {
        configService.setIfMissing(KEY_VERTEX_ENDPOINT, vertex.getEndpoint());
        configService.setIfMissing(KEY_VERTEX_DISPLAY_NAME, vertex.getDisplayName());
        configService.setIfMissing(KEY_VERTEX_DESCRIPTION, vertex.getDescription());
        configService.setIfMissing(KEY_FEDERATION_PROTOCOL, FederationProtocol.HTTP.name());
        configService.setIfMissing(KEY_JWT_SIGNING_KEY, Random.generateRandomString(128));
    }

    public Vertex getVertex() {
        return Vertex.builder()
                .endpoint(configService.getValue(KEY_VERTEX_ENDPOINT, "localhost:8080"))
                .displayName(configService.getValue(KEY_VERTEX_DISPLAY_NAME, "Vertex"))
                .description(configService.getValue(KEY_VERTEX_DESCRIPTION, "Vertex"))
                .build();
    }

    public String getJwtSigningKey() {
        return configService.getValue(KEY_JWT_SIGNING_KEY, "secret");
    }

    public String getVertexEndpoint() {
        return configService.getValue(KEY_VERTEX_ENDPOINT, "localhost:8080");
    }

    public void setVertexEndpoint(String endpoint) {
        configService.set(KEY_VERTEX_ENDPOINT, endpoint);
    }

    public void setVertexDisplayName(String displayName) {
        configService.set(KEY_VERTEX_DISPLAY_NAME, displayName);
    }

    public void setVertexDescription(String description) {
        configService.set(KEY_VERTEX_DESCRIPTION, description);
    }

    public VertexFederationProtocolResponse getFederationProtocol() {
        String value = configService.getValue(KEY_FEDERATION_PROTOCOL, "http");
        return VertexFederationProtocolResponse.builder().federationProtocol(FederationProtocol.valueOf(value)).build();
    }

    public void setFederationProtocol(FederationProtocol federationProtocol) {
        configService.set(KEY_FEDERATION_PROTOCOL, federationProtocol.name());
    }

    public void resetJwtSigningKey() {
        String jwtSigningKey = Random.generateRandomString(128);
        configService.set(KEY_JWT_SIGNING_KEY, jwtSigningKey);
    }
}
