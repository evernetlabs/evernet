package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import xyz.evernet.bean.NodeAddress;
import xyz.evernet.bean.UserAddress;
import xyz.evernet.model.Node;
import xyz.evernet.model.Structure;
import xyz.evernet.repository.NodeRepository;
import xyz.evernet.request.NodeCreationRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    private final StructureRouterService structureRouterService;

    private final NodeFederationService nodeFederationService;

    private final ConfigReaderService configReaderService;

    public Node create(NodeCreationRequest request, String creator) {
        String currentVertexEndpoint = configReaderService.getVertexEndpoint();

        UserAddress creatorAddress = UserAddress.builder()
                .vertexEndpoint(currentVertexEndpoint)
                .username(creator)
                .build();

        Structure structure = structureRouterService.get(request.getStructureAddress());

        Map<String, Set<String>> users = new HashMap<>(request.getUsers());

        Set<String> creatorRoles = users.get(creatorAddress.toString());
        if (creatorRoles != null) {
            creatorRoles.add("creator");
        } else {
            users.put(creatorAddress.toString(), Set.of("creator"));
        }

        ObjectId nodeId = ObjectId.get();
        Node node = Node.builder()
                .id(nodeId.toString())
                .address(
                        NodeAddress
                                .builder()
                                .id(nodeId.toString())
                                .vertexEndpoint(currentVertexEndpoint)
                                .build()
                                .toString()
                )
                .structureAddress(request.getStructureAddress())
                .properties(structure.validateProperties(request.getProperties()))
                .users(users)
                .creatorAddress(creatorAddress.toString())
                .build();

        return nodeRepository.save(node);
    }
}
