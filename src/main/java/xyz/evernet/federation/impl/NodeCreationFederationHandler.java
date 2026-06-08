package xyz.evernet.federation.impl;

import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeCreationEvent;

import java.util.Set;

public class NodeCreationFederationHandler implements FederationHandler<NodeCreationEvent> {

    @Override
    public void transmit(NodeCreationEvent event, String requesterAddress, String sourceVertexEndpoint, Set<String> targetUserAddresses, String federationToken) {

    }

    @Override
    public void receive(NodeCreationEvent event, String requesterAddress, Set<String> targetUserAddresses) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeCreationEvent.class;
    }
}
