package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeRelationshipUnsetEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeRelationshipUnsetFederationHandler implements FederationHandler<NodeRelationshipUnsetEvent> {

    @Override
    public void transmit(NodeRelationshipUnsetEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodeRelationshipUnsetEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeRelationshipUnsetEvent.class;
    }
}
