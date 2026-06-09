package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.enums.NodeEventType;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.NodeFederationClient;
import xyz.evernet.federation.event.FederationEventEnvelope;
import xyz.evernet.federation.event.NodeCreationEvent;
import xyz.evernet.model.Node;
import xyz.evernet.repository.NodeRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeCreationFederationHandler implements FederationHandler<NodeCreationEvent> {

    private final NodeFederationClient nodeFederationClient;

    private final NodeRepository nodeRepository;

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

        Node replicatedNode = nodeRepository.findByAddress(node.getAddress());

        if (replicatedNode != null) {
            return;
        }

        replicatedNode = Node.builder()
                .address(node.getAddress())
                .structureAddress(node.getStructureAddress())
                .properties(node.getProperties())
                .users(node.getUsers())
                .creatorAddress(node.getCreatorAddress())
                .build();

        nodeRepository.save(replicatedNode);
    }

    @Override
    public Class<?> getEventType() {
        return NodeCreationEvent.class;
    }
}
