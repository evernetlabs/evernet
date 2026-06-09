package xyz.evernet.federation;

import java.util.Set;

public interface FederationHandler<E> {

    void transmit(E event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses);

    void receive(E event, String requesterAddress, Set<String> targetUserAddresses);

    Class<?> getEventType();
}
