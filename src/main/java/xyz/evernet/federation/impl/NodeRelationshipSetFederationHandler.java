package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeRelationshipSetEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeRelationshipSetFederationHandler implements FederationHandler<NodeRelationshipSetEvent> {

    @Override
    public void transmit(NodeRelationshipSetEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodeRelationshipSetEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeRelationshipSetEvent.class;
    }
}
