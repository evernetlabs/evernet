package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeDeletionEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeDeletionFederationHandler implements FederationHandler<NodeDeletionEvent> {

    @Override
    public void transmit(NodeDeletionEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodeDeletionEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeDeletionEvent.class;
    }
}
