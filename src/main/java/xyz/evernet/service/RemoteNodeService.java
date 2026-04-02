package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.evernet.model.Node;

@Service
@RequiredArgsConstructor
public class RemoteNodeService {

    private final RestTemplate restTemplate;

    private final VertexConfigService vertexConfigService;

    public Node get(String vertexEndpoint, String nodeIdentifier) {
        return restTemplate.getForObject(String.format("%s://%s/api/v1/nodes/%s",
                vertexConfigService.getFederationProtocol().getFederationProtocol().name().toLowerCase(),
                vertexEndpoint,
                nodeIdentifier), Node.class);
    }
}
