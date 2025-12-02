package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.RelationshipChain;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Transition;
import org.evernet.repository.TransitionRepository;
import org.evernet.request.TransitionCreationRequest;
import org.evernet.request.TransitionUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransitionService {

    private final TransitionRepository transitionRepository;

    private final StateService stateService;

    private final EventService eventService;

    private final RelationshipService relationshipService;

    public Transition create(String nodeIdentifier, String structureAddress, TransitionCreationRequest request, String creator) {
        if (!stateService.exists(request.getSourceStateIdentifier(), structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Source state %s not found for structure %s on node %s", request.getSourceStateIdentifier(), structureAddress, nodeIdentifier));
        }

        if (!stateService.exists(request.getTargetStateIdentifier(), structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Target state %s not found for structure %s on node %s", request.getTargetStateIdentifier(), structureAddress, nodeIdentifier));
        }

        RelationshipChain eventParentReferenceChain = RelationshipChain.fromString(request.getEventParentReferenceChain());

        String eventParentStructureAddress = structureAddress;
        if (!CollectionUtils.isEmpty(eventParentReferenceChain.getNodes())) {
            eventParentStructureAddress = relationshipService.walk(structureAddress, eventParentReferenceChain, nodeIdentifier).getToStructureAddress();
        }

        if (!eventService.exists(request.getEventIdentifier(), eventParentStructureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Event %s not found for structure %s on node %s", request.getEventIdentifier(), eventParentStructureAddress, nodeIdentifier));
        }

        if (transitionRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(
                nodeIdentifier,
                structureAddress,
                request.getIdentifier()
        )) {
            throw new ClientException(String.format("Transition %s already exists for structure %s on node %s", request.getEventIdentifier(), eventParentStructureAddress, nodeIdentifier));
        }

        if (transitionRepository.existsByNodeIdentifierAndStructureAddressAndSourceStateIdentifierAndEventParentReferenceChainAndEventIdentifier(
                nodeIdentifier,
                structureAddress,
                request.getSourceStateIdentifier(),
                request.getEventParentReferenceChain(),
                request.getEventIdentifier()
        )) {
            throw new ClientException(String.format("Event %s (%s) is already associated with a transition from state %s for structure %s on node %s",
                    request.getEventIdentifier(),
                    request.getEventParentReferenceChain(),
                    request.getSourceStateIdentifier(),
                    structureAddress,
                    nodeIdentifier));
        }

        Transition transition = Transition.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .sourceStateIdentifier(request.getSourceStateIdentifier())
                .targetStateIdentifier(request.getTargetStateIdentifier())
                .eventParentReferenceChain(request.getEventParentReferenceChain())
                .eventIdentifier(request.getEventIdentifier())
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .creator(creator)
                .build();

        return transitionRepository.save(transition);
    }

    public List<Transition> list(String nodeIdentifier, String structureAddress) {
        return transitionRepository.findByNodeIdentifierAndStructureAddress(nodeIdentifier, structureAddress);
    }

    public Transition get(String identifier, String structureAddress, String nodeIdentifier) {
        Transition transition = transitionRepository.findByIdentifierAndStructureAddressAndNodeIdentifier(
                identifier,
                structureAddress,
                nodeIdentifier
        );

        if (transition == null) {
            throw new NotFoundException(String.format("Transition %s not found for structure %s on node %s", identifier, structureAddress, nodeIdentifier));
        }

        return transition;
    }

    public Transition update(String identifier, TransitionUpdateRequest request, String structureAddress, String nodeIdentifier) {
        Transition transition = get(identifier, structureAddress, nodeIdentifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            transition.setDisplayName(request.getDisplayName());
        }

        transition.setDescription(request.getDescription());
        return transitionRepository.save(transition);
    }

    public Transition delete(String identifier, String structureAddress, String nodeIdentifier) {
        Transition transition = get(identifier, structureAddress, nodeIdentifier);
        transitionRepository.delete(transition);
        return transition;
    }

    public Boolean exists(String identifier, String structureAddress, String nodeIdentifier) {
        return transitionRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(
                nodeIdentifier,
                structureAddress,
                identifier
        );
    }
}
