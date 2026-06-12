package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.enums.NodeEventType;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.NodeFederationClient;
import xyz.evernet.federation.event.FederationEventEnvelope;
import xyz.evernet.federation.event.NodePropertyUnsetEvent;
import xyz.evernet.service.NodeHelperService;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodePropertyUnsetFederationHandler implements FederationHandler<NodePropertyUnsetEvent> {

    private final NodeHelperService nodeHelperService;

    private final NodeFederationClient nodeFederationClient;

    @Override
    public void transmit(NodePropertyUnsetEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {
        FederationEventEnvelope<NodePropertyUnsetEvent> envelope = new FederationEventEnvelope<>();
        envelope.setEvent(event);
        envelope.setEventType(NodeEventType.PROPERTY_UNSET);
        envelope.setRequesterAddress(requesterAddress);
        envelope.setTargetUserAddresses(targetUserAddresses);

        nodeFederationClient.send(envelope, targetVertexEndpoint);
    }

    @Override
    public void receive(NodePropertyUnsetEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {
        if (receivedLocally) {
            return;
        }

        nodeHelperService.unsetProperty(
                event.getNodeAddress(),
                event.getPropertyIdentifier(),
                requesterAddress
        );
    }

    @Override
    public Class<?> getEventType() {
        return NodePropertyUnsetEvent.class;
    }
}
