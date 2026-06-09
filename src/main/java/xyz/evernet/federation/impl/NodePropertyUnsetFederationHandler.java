package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodePropertyUnsetEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodePropertyUnsetFederationHandler implements FederationHandler<NodePropertyUnsetEvent> {

    @Override
    public void transmit(NodePropertyUnsetEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodePropertyUnsetEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodePropertyUnsetEvent.class;
    }
}
