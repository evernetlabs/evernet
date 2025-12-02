package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.State;
import org.evernet.repository.StateRepository;
import org.evernet.request.StateCreationRequest;
import org.evernet.request.StateUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;

    private final StructureService structureService;

    public State create(String nodeIdentifier, String structureAddress, StateCreationRequest request, String creator) {
        if (!structureService.exists(structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Structure %s not found on node %s", structureAddress, nodeIdentifier));
        }

        if (stateRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(nodeIdentifier, structureAddress, request.getIdentifier())) {
            throw new ClientException(String.format("State %s already exists for structure %s on node %s" , request.getIdentifier(), structureAddress, nodeIdentifier));
        }

        State state = State.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .creator(creator)
                .build();

        return stateRepository.save(state);
    }

    public List<State> list(String nodeIdentifier, String structureAddress) {
        return stateRepository.findByNodeIdentifierAndStructureAddress(nodeIdentifier, structureAddress);
    }

    public State get(String identifier, String structureAddress, String nodeIdentifier) {
        State state = stateRepository.findByIdentifierAndStructureAddressAndNodeIdentifier(identifier, structureAddress, nodeIdentifier);

        if (state == null) {
            throw new NotFoundException(String.format("State %s not found for structure %s on node %s", identifier, structureAddress, nodeIdentifier));
        }

        return state;
    }

    public State update(String identifier, StateUpdateRequest request, String structureAddress, String nodeIdentifier) {
        State state = get(identifier, structureAddress, nodeIdentifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            state.setDisplayName(request.getDisplayName());
        }

        state.setDescription(request.getDescription());
        return stateRepository.save(state);
    }

    public State delete(String identifier, String structureAddress, String nodeIdentifier) {
        State state = get(identifier, structureAddress, nodeIdentifier);
        stateRepository.delete(state);
        return state;
    }

    public Boolean exists(String identifier, String structureAddress, String nodeIdentifier) {
        return stateRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(nodeIdentifier, structureAddress, identifier);
    }
}
