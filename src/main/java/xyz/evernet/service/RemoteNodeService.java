package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Node;
import xyz.evernet.model.Structure;
import xyz.evernet.request.StructuresFetchRequest;
import xyz.evernet.response.StructuresFetchResponse;

import java.util.List;
import java.util.Set;

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

    public List<Structure> getStructures(String vertexEndpoint, String nodeIdentifier, Set<String> structureAddresses) {
        StructuresFetchResponse response = restTemplate.postForObject(
                String.format(
                        "%s://%s/api/v1/nodes/%s/structures/fetch",
                        vertexConfigService.getFederationProtocol().getFederationProtocol().name().toLowerCase(),
                        vertexEndpoint,
                        nodeIdentifier
                ),
                new HttpEntity<>(new StructuresFetchRequest(structureAddresses)),
                StructuresFetchResponse.class
        );

        if (response == null) {
            throw new NotFoundException(String.format("Structures %s not found on node %s of vertex %s", String.join(", ", structureAddresses), nodeIdentifier, vertexEndpoint));
        }

        return response.getStructures();
    }
}
