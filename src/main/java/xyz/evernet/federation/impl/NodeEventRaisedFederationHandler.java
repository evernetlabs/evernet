package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeEventRaisedEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeEventRaisedFederationHandler implements FederationHandler<NodeEventRaisedEvent> {

    @Override
    public void transmit(NodeEventRaisedEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodeEventRaisedEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeEventRaisedEvent.class;
    }
}
