package xyz.evernet.federation.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import xyz.evernet.enums.NodeEventType;
import xyz.evernet.exception.NotAllowedException;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.NodeFederationClient;
import xyz.evernet.federation.event.FederationEventEnvelope;
import xyz.evernet.federation.event.NodeDeletionEvent;
import xyz.evernet.service.NodeHelperService;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class NodeDeletionFederationHandler implements FederationHandler<NodeDeletionEvent> {

    private final NodeFederationClient nodeFederationClient;

    private final NodeHelperService nodeHelperService;

    @Override
    public void transmit(NodeDeletionEvent event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception {
        FederationEventEnvelope<NodeDeletionEvent> envelope = new FederationEventEnvelope<>();
        envelope.setEvent(event);
        envelope.setEventType(NodeEventType.NODE_DELETED);
        envelope.setRequesterAddress(requesterAddress);
        envelope.setTargetUserAddresses(targetUserAddresses);

        nodeFederationClient.send(envelope, targetVertexEndpoint);
    }

    @Override
    public void receive(NodeDeletionEvent event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally) {
        if (receivedLocally) {
            return;
        }

        if (!nodeHelperService.hasManagementRole(event.getNodeAddress(), requesterAddress)) {
            throw new NotAllowedException();
        }

        nodeHelperService.delete(event.getNodeAddress());
    }

    @Override
    public Class<?> getEventType() {
        return NodeDeletionEvent.class;
    }
}
