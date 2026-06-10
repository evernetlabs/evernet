package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.event.NodeFunctionExecutedEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeFunctionExecutedFederationHandler implements FederationHandler<NodeFunctionExecutedEvent> {

    @Override
    public void transmit(NodeFunctionExecutedEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {

    }

    @Override
    public void receive(NodeFunctionExecutedEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {

    }

    @Override
    public Class<?> getEventType() {
        return NodeFunctionExecutedEvent.class;
    }
}
