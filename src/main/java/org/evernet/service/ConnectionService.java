package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.RelationshipChain;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Connection;
import org.evernet.repository.ConnectionRepository;
import org.evernet.request.ConnectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    private final StructureService structureService;

    private final RelationshipService relationshipService;

    private final EventService eventService;

    private final FunctionService functionService;

    private final WorkflowService workflowService;

    public Connection create(String nodeIdentifier, String structureAddress, ConnectionRequest request, String creator) {
        if (!structureService.exists(structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Structure %s not found on node %s",  structureAddress, nodeIdentifier));
        }

        if (connectionRepository.existsByNodeIdentifierAndStructureAddressAndEventParentReferenceChainAndEventIdentifierAndActionParentReferenceChainAndActionTypeAndActionIdentifier(
                nodeIdentifier,
                structureAddress,
                request.getEventParentReferenceChain(),
                request.getEventIdentifier(),
                request.getActionParentReferenceChain(),
                request.getActionType(),
                request.getActionIdentifier()
        )) {
            throw new ClientException(String.format("Connection from event %s (%s) to action %s (%s) of type %s already exists on structure %s on node %s",
                    request.getEventIdentifier(),
                    request.getEventParentReferenceChain(),
                    request.getActionIdentifier(),
                    request.getActionParentReferenceChain(),
                    request.getActionType(),
                    structureAddress,
                    nodeIdentifier));
        }

        RelationshipChain eventParentReferenceChain = RelationshipChain.fromString(request.getEventParentReferenceChain());

        String eventParentStructureAddress = structureAddress;
        if (!CollectionUtils.isEmpty(eventParentReferenceChain.getNodes())) {
            eventParentStructureAddress = relationshipService.walk(structureAddress, eventParentReferenceChain, nodeIdentifier).getToStructureAddress();
        }

        if (!eventService.exists(request.getEventIdentifier(), eventParentStructureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Event %s not found for structure %s on node %s", request.getEventIdentifier(), eventParentStructureAddress, nodeIdentifier));
        }

        RelationshipChain actionParentReferenceChain = RelationshipChain.fromString(request.getActionParentReferenceChain());
        String actionParentStructureAddress = structureAddress;
        if (!CollectionUtils.isEmpty(actionParentReferenceChain.getNodes())) {
            actionParentStructureAddress = relationshipService.walk(structureAddress, actionParentReferenceChain, nodeIdentifier).getToStructureAddress();
        }

        switch (request.getActionType()) {
            case FUNCTION -> {
                if (!functionService.exists(request.getActionIdentifier(), actionParentStructureAddress, nodeIdentifier)) {
                    throw new NotFoundException(String.format("Function %s not found for structure %s on node %s", request.getActionIdentifier(), actionParentStructureAddress, nodeIdentifier));
                }
            }

            case WORKFLOW -> {
                if (!workflowService.exists(request.getActionIdentifier(), actionParentStructureAddress, nodeIdentifier)) {
                    throw new NotFoundException(String.format("Workflow %s not found for structure %s on node %s",  request.getActionIdentifier(), actionParentStructureAddress, nodeIdentifier));
                }
            }

            default -> throw new ClientException(String.format("Unknown action type %s", request.getActionType()));
        }

        Connection connection = Connection.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .eventParentReferenceChain(eventParentStructureAddress)
                .eventIdentifier(request.getEventIdentifier())
                .actionParentReferenceChain(request.getActionParentReferenceChain())
                .actionIdentifier(request.getActionIdentifier())
                .actionType(request.getActionType())
                .creator(creator)
                .build();

        return connectionRepository.save(connection);
    }

    public List<Connection> list(String nodeIdentifier, String structureAddress) {
        return connectionRepository.findByNodeIdentifierAndStructureAddress(nodeIdentifier, structureAddress);
    }

    public Connection delete(String nodeIdentifier, String structureAddress, ConnectionRequest request) {
        Connection connection = connectionRepository.findByNodeIdentifierAndStructureAddressAndEventParentReferenceChainAndEventIdentifierAndActionParentReferenceChainAndActionTypeAndActionIdentifier(
                nodeIdentifier,
                structureAddress,
                request.getEventParentReferenceChain(),
                request.getEventIdentifier(),
                request.getActionParentReferenceChain(),
                request.getActionType(),
                request.getActionIdentifier()
        );

        if (connection == null) {
            throw new NotFoundException(String.format("Connection from event %s (%s) to action %s (%s) of type %s not found on structure %s on node %s",
                    request.getEventIdentifier(),
                    request.getEventParentReferenceChain(),
                    request.getActionIdentifier(),
                    request.getActionParentReferenceChain(),
                    request.getActionType(),
                    structureAddress,
                    nodeIdentifier));
        }

        connectionRepository.delete(connection);
        return connection;
    }
}
