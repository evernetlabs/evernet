package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.enums.NodeEventType;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.NodeFederationClient;
import xyz.evernet.federation.event.FederationEventEnvelope;
import xyz.evernet.federation.event.NodeCreationEvent;
import xyz.evernet.model.Node;
import xyz.evernet.model.Structure;
import xyz.evernet.service.NodeHelperService;
import xyz.evernet.service.StructureRouterService;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeCreationFederationHandler implements FederationHandler<NodeCreationEvent> {

    private final NodeFederationClient nodeFederationClient;

    private final StructureRouterService structureRouterService;

    private final NodeHelperService nodeHelperService;

    @Override
    public void transmit(NodeCreationEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {
        FederationEventEnvelope<NodeCreationEvent> envelope = new FederationEventEnvelope<>();
        envelope.setEvent(event);
        envelope.setEventType(NodeEventType.NODE_CREATED);
        envelope.setRequesterAddress(requesterAddress);
        envelope.setTargetUserAddresses(targetUserAddresses);

        nodeFederationClient.send(envelope, targetVertexEndpoint);
    }

    @Override
    public void receive(NodeCreationEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {
        if (receivedLocally) {
            return;
        }

        Node node = event.getNode();

        Structure structure = structureRouterService.get(node.getStructureAddress());

        Node replicatedNode = nodeHelperService.findByAddress(node.getAddress());

        if (replicatedNode != null) {
            return;
        }

        replicatedNode = Node.builder()
                .address(node.getAddress())
                .structureAddress(node.getStructureAddress())
                .properties(structure.validateProperties(node.getProperties()))
                .users(node.getUsers())
                .creatorAddress(node.getCreatorAddress())
                .build();

        nodeHelperService.save(replicatedNode);
    }

    @Override
    public Class<?> getEventType() {
        return NodeCreationEvent.class;
    }
}
