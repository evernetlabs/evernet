package xyz.evernet.federation;

import java.util.Set;

public interface FederationHandler<E> {

    void transmit(E event, String requesterAddress, String sourceVertexEndpoint, String targetVertexendpoint, Set<String> targetUserAddresses, String federationToken);

    void receive(E event, String requesterAddress, Set<String> targetUserAddresses);

    Class<?> getEventType();
}
