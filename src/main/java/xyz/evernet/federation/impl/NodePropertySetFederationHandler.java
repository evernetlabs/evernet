package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.enums.NodeEventType;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.NodeFederationClient;
import xyz.evernet.federation.event.FederationEventEnvelope;
import xyz.evernet.federation.event.NodePropertySetEvent;
import xyz.evernet.service.NodeHelperService;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodePropertySetFederationHandler implements FederationHandler<NodePropertySetEvent> {

    private final NodeHelperService nodeHelperService;

    private final NodeFederationClient nodeFederationClient;

    @Override
    public void transmit(NodePropertySetEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {
        FederationEventEnvelope<NodePropertySetEvent> envelope = new FederationEventEnvelope<>();
        envelope.setEvent(event);
        envelope.setEventType(NodeEventType.PROPERTY_SET);
        envelope.setRequesterAddress(requesterAddress);
        envelope.setTargetUserAddresses(targetUserAddresses);

        nodeFederationClient.send(envelope, targetVertexEndpoint);
    }

    @Override
    public void receive(NodePropertySetEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {
        if (receivedLocally) {
            return;
        }

        nodeHelperService.setProperty(event.getNodeAddress(), event.getPropertyIdentifier(), event.getPropertyValue(), requesterAddress);
    }

    @Override
    public Class<?> getEventType() {
        return NodePropertySetEvent.class;
    }
}
