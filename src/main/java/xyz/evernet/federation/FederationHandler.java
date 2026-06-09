package xyz.evernet.federation;

import java.util.Set;

public interface FederationHandler<E> {

    void transmit(E event, String requesterAddress, String sourceVertexEndpoint, String targetVertexEndpoint, Set<String> targetUserAddresses) throws Exception;

    void receive(E event, String requesterAddress, Set<String> targetUserAddresses, Boolean receivedLocally);

    Class<?> getEventType();
}
