package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeUserAdditionEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeUserAdditionFederationHandler implements FederationHandler<NodeUserAdditionEvent> {

    @Override
    public void transmit(NodeUserAdditionEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodeUserAdditionEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeUserAdditionEvent.class;
    }

}
