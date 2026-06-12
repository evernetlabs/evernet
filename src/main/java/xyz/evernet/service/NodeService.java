package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import xyz.evernet.bean.NodeAddress;
import xyz.evernet.bean.UserAddress;
import xyz.evernet.exception.NotAllowedException;
import xyz.evernet.federation.event.NodeCreationEvent;
import xyz.evernet.federation.event.NodeDeletionEvent;
import xyz.evernet.federation.event.NodePropertySetEvent;
import xyz.evernet.federation.event.NodePropertyUnsetEvent;
import xyz.evernet.model.Node;
import xyz.evernet.model.Structure;
import xyz.evernet.request.NodeCreationRequest;
import xyz.evernet.request.SetNodePropertyRequest;
import xyz.evernet.request.UnsetNodePropertyRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeHelperService nodeHelperService;

    private final StructureRouterService structureRouterService;

    private final NodeFederationService nodeFederationService;

    private final ConfigReaderService configReaderService;

    public Node create(NodeCreationRequest request, String creator) throws Exception {
        String currentVertexEndpoint = configReaderService.getVertexEndpoint();

        UserAddress creatorAddress = UserAddress.builder()
                .vertexEndpoint(currentVertexEndpoint)
                .username(creator)
                .build();

        Structure structure = structureRouterService.get(request.getStructureAddress());

        if (request.getUsers() == null) {
            request.setUsers(new HashMap<>());
        }

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

        node = nodeHelperService.save(node);

        nodeFederationService.transmitEvent(node, NodeCreationEvent.builder()
                        .node(node)
                        .build(),
                creatorAddress.toString()
        );

        return node;
    }

    public void delete(String nodeAddress, String requester) throws Exception {
        UserAddress requesterAddress = UserAddress.builder().username(requester).vertexEndpoint(configReaderService.getVertexEndpoint()).build();

        Node node = nodeHelperService.findByAddress(nodeAddress);

        if (!nodeHelperService.hasManagementRole(node, requesterAddress.toString())) {
            throw new NotAllowedException();
        }

        nodeHelperService.delete(nodeAddress);

        nodeFederationService.transmitEvent(node, NodeDeletionEvent.builder().nodeAddress(node.getAddress()).build(), requesterAddress.toString());
    }

    public void setProperty(String nodeAddress, SetNodePropertyRequest request, String requester) throws Exception {
        UserAddress requesterAddress = UserAddress.builder().username(requester).vertexEndpoint(configReaderService.getVertexEndpoint()).build();

        Node node = nodeHelperService.setProperty(nodeAddress, request.getPropertyIdentifier(), request.getValue(), requesterAddress.toString());

        nodeFederationService.transmitEvent(node, NodePropertySetEvent
                        .builder()
                        .nodeAddress(nodeAddress)
                        .propertyIdentifier(request.getPropertyIdentifier())
                        .propertyValue(request.getValue())
                        .build(),
                requesterAddress.toString());
    }

    public void unsetProperty(String nodeAddress, UnsetNodePropertyRequest request, String requester) throws Exception {
        UserAddress requesterAddress = UserAddress.builder().username(requester).vertexEndpoint(configReaderService.getVertexEndpoint()).build();

        Node node = nodeHelperService.unsetProperty(nodeAddress, request.getPropertyIdentifier(), requesterAddress.toString());

        nodeFederationService.transmitEvent(node, NodePropertyUnsetEvent.builder()
                        .nodeAddress(node.getAddress())
                        .propertyIdentifier(request.getPropertyIdentifier())
                        .build(),
                requesterAddress.toString());
    }
}
