package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.RelationshipChain;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.TransitionAction;
import org.evernet.repository.TransitionActionRepository;
import org.evernet.request.TransitionActionRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransitionActionService {

    private final TransitionActionRepository transitionActionRepository;

    private final TransitionService transitionService;

    private final RelationshipService relationshipService;

    private final FunctionService functionService;

    private final WorkflowService workflowService;

    public TransitionAction create(String nodeIdentifier, String structureAddress, String transitionIdentifier, TransitionActionRequest request, String creator) {
        if (!transitionService.exists(transitionIdentifier, structureAddress, nodeIdentifier)) {
            throw new ClientException(String.format("Transition %s not found for structure %s on node %s", transitionIdentifier, structureAddress, nodeIdentifier));
        }

        if (transitionActionRepository.existsByNodeIdentifierAndStructureAddressAndTransitionIdentifierAndActionParentReferenceChainAndActionTypeAndActionIdentifier(
                nodeIdentifier,
                structureAddress,
                transitionIdentifier,
                request.getActionParentReferenceChain(),
                request.getActionType(),
                request.getActionIdentifier()
        )) {
            throw new ClientException(String.format("Action %s (%s) of type %s already exists for transition %s for structure %s on node %s",
                    request.getActionIdentifier(),
                    request.getActionParentReferenceChain(),
                    request.getActionType(),
                    transitionIdentifier,
                    structureAddress,
                    nodeIdentifier));
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

        TransitionAction transitionAction = TransitionAction.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .transitionIdentifier(transitionIdentifier)
                .actionParentReferenceChain(request.getActionParentReferenceChain())
                .actionType(request.getActionType())
                .actionIdentifier(request.getActionIdentifier())
                .creator(creator)
                .build();

        return transitionActionRepository.save(transitionAction);
    }

    public List<TransitionAction> list(String nodeIdentifier, String structureAddress, String transitionIdentifier) {
        return transitionActionRepository.findByNodeIdentifierAndStructureAddressAndTransitionIdentifier(
                nodeIdentifier,
                structureAddress,
                transitionIdentifier
        );
    }

    public TransitionAction delete(String nodeIdentifier, String structureAddress, String transitionIdentifier, TransitionActionRequest request) {
        TransitionAction transitionAction = transitionActionRepository.findByNodeIdentifierAndStructureAddressAndTransitionIdentifierAndActionParentReferenceChainAndActionTypeAndActionIdentifier(
                nodeIdentifier,
                structureAddress,
                transitionIdentifier,
                request.getActionParentReferenceChain(),
                request.getActionType(),
                request.getActionIdentifier()
        );

        if (transitionAction == null) {
            throw new NotFoundException(String.format("Action %s (%s) of type %s not found for transition %s for structure %s on node %s",
                    request.getActionIdentifier(),
                    request.getActionParentReferenceChain(),
                    request.getActionType(),
                    transitionIdentifier,
                    structureAddress,
                    nodeIdentifier));
        }

        transitionActionRepository.delete(transitionAction);
        return transitionAction;
    }
}
