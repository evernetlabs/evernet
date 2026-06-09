package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.evernet.bean.UserAddress;
import xyz.evernet.federation.FederationHandler;
import xyz.evernet.federation.FederationHandlerFactory;
import xyz.evernet.model.Node;

import java.util.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
@Slf4j
public class NodeFederationService {

    private final FederationHandlerFactory federationHandlerFactory;

    private final ConfigReaderService configReaderService;

    @Async
    public <E> void transmitEvent(Node node, E event, String requesterAddress) throws Exception {
        FederationHandler<E> federationHandler = (FederationHandler<E>) federationHandlerFactory.getHandler(event.getClass());

        Map<String, Set<String>> users = node.getUsers();

        if (CollectionUtils.isEmpty(users)) {
            return;
        }

        String currentVertexEndpoint = configReaderService.getVertexEndpoint();

        Set<String> localUsers = new HashSet<>();
        Map<String, Set<String>> remoteUsers = new HashMap<>();

        for (String targetUserAddressStr : users.keySet()) {
            if (targetUserAddressStr.equals(requesterAddress)) {
                continue;
            }

            UserAddress targetUserAddress = UserAddress.from(targetUserAddressStr);

            if (targetUserAddress.getVertexEndpoint().equals(currentVertexEndpoint)) {
                localUsers.add(targetUserAddressStr);
            } else {
                Set<String> remoteUsersForVertex = remoteUsers.computeIfAbsent(targetUserAddress.getVertexEndpoint(), k -> new HashSet<>());
                remoteUsersForVertex.add(targetUserAddressStr);
            }
        }

        if (!localUsers.isEmpty()) {
            federationHandler.receive(event, requesterAddress, localUsers, true);
        }

        if (!remoteUsers.isEmpty()) {
            for (Map.Entry<String, Set<String>> entry : remoteUsers.entrySet()) {
                try {
                    federationHandler
                            .transmit(
                                    event,
                                    requesterAddress,
                                    currentVertexEndpoint,
                                    entry.getKey(),
                                    entry.getValue()
                            );
                }  catch (Exception e) {
                    log.error("Error transmitting event", e);
                }
            }
        }
    }

    @Async
    public <E> void receiveEvent(E event, Set<String> targetUserAddresses, String sourceVertexEndpoint, String requesterAddress) {
        UserAddress requesterAddressObj = UserAddress.from(requesterAddress);

        if (!requesterAddressObj.getVertexEndpoint().equals(sourceVertexEndpoint)) {
            return;
        }

        String currentVertexEndpoint = configReaderService.getVertexEndpoint();

        Set<String> filteredTargetUserAddresses = new HashSet<>();

        for (String targetUserAddressStr : targetUserAddresses) {
            UserAddress targetUserAddress = UserAddress.from(targetUserAddressStr);

            if (targetUserAddress.getVertexEndpoint().equals(currentVertexEndpoint)) {
                filteredTargetUserAddresses.add(targetUserAddressStr);
            }
        }

        if (CollectionUtils.isEmpty(filteredTargetUserAddresses)) {
            return;
        }

        FederationHandler<E> federationHandler = (FederationHandler<E>) federationHandlerFactory.getHandler(event.getClass());
        federationHandler.receive(event, requesterAddress, filteredTargetUserAddresses, false);
    }
}
