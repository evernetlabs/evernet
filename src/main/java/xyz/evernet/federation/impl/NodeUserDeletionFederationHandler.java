package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeUserDeletionEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeUserDeletionFederationHandler implements FederationHandler<NodeUserDeletionEvent> {

    @Override
    public void transmit(NodeUserDeletionEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodeUserDeletionEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeUserDeletionEvent.class;
    }

}
