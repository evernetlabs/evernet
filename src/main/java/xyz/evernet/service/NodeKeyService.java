package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.evernet.exception.ServerException;
import xyz.evernet.response.VertexSigningPublicKeyResponse;
import xyz.evernet.util.Ed25519KeyHelper;

import java.security.PublicKey;

@Service
@RequiredArgsConstructor
public class NodeKeyService {

    private final RestTemplate restTemplate;

    private final ConfigReaderService configReaderService;

    public PublicKey getSigningPublicKey(String vertexEndpoint) throws Exception {
        String url = "%s://%s/api/v1/configs/signing-public-key"
                .formatted(
                        configReaderService.getFederationProtocol(),
                        vertexEndpoint
                );

        VertexSigningPublicKeyResponse response = restTemplate.getForObject(url, VertexSigningPublicKeyResponse.class);

        if (response == null) {
            throw new ServerException("Error fetching signing public key from vertex %s".formatted(vertexEndpoint));
        }

        return Ed25519KeyHelper.stringToPublicKey(response.getSigningPublicKey());
    }
}
