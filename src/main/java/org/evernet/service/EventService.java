package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Event;
import org.evernet.repository.EventRepository;
import org.evernet.request.EventCreationRequest;
import org.evernet.request.EventUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final StructureService structureService;

    public Event create(String nodeIdentifier, String structureAddress, EventCreationRequest request, String creator) {
        if (!structureService.exists(structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Structure %s not found on node %s", structureAddress, nodeIdentifier));
        }

        if (eventRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(nodeIdentifier, structureAddress, request.getIdentifier())) {
            throw new ClientException(String.format("Event %s already exists for structure %s on node %s", request.getIdentifier(), structureAddress, nodeIdentifier));
        }

        Event event = Event.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .dataFormat(request.getDataFormat())
                .dataSchema(request.getDataSchema())
                .creator(creator)
                .build();

        return eventRepository.save(event);
    }

    public List<Event> list(String nodeIdentifier, String structureAddress) {
        return eventRepository.findByNodeIdentifierAndStructureAddress(nodeIdentifier, structureAddress);
    }

    public Event get(String identifier, String structureAddress, String nodeIdentifier) {
        Event event = eventRepository.findByIdentifierAndStructureAddressAndNodeIdentifier(identifier, structureAddress, nodeIdentifier);

        if (event == null) {
            throw new NotFoundException(String.format("Event %s not found for structure %s on node %s", identifier, structureAddress, nodeIdentifier));
        }

        return event;
    }

    public Event update(String identifier, EventUpdateRequest request, String structureAddress, String nodeIdentifier) {
        Event event = get(identifier, structureAddress, nodeIdentifier);

        if (request.getDataFormat() != null) {
            event.setDataFormat(request.getDataFormat());
        }

        event.setDataSchema(request.getDataSchema());

        if (StringUtils.hasText(request.getDisplayName())) {
            event.setDisplayName(request.getDisplayName());
        }

        event.setDescription(request.getDescription());

        return eventRepository.save(event);
    }

    public Event delete(String identifier, String structureAddress, String nodeIdentifier) {
        Event event = get(identifier, structureAddress, nodeIdentifier);
        eventRepository.delete(event);
        return event;
    }

    public Boolean exists(String identifier, String structureAddress, String nodeIdentifier) {
        return eventRepository.existsByIdentifierAndStructureAddressAndNodeIdentifier(identifier, structureAddress, nodeIdentifier);
    }
}
