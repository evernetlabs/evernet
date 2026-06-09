package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeUserUpdateEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeUserUpdateFederationHandler implements FederationHandler<NodeUserUpdateEvent> {

    @Override
    public void transmit(NodeUserUpdateEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodeUserUpdateEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeUserUpdateEvent.class;
    }

}
