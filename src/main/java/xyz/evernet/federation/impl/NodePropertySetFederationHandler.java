package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodePropertySetEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodePropertySetFederationHandler implements FederationHandler<NodePropertySetEvent> {

    @Override
    public void transmit(NodePropertySetEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodePropertySetEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodePropertySetEvent.class;
    }
}
