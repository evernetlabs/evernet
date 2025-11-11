package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Property;
import org.evernet.repository.PropertyRepository;
import org.evernet.request.PropertyCreationRequest;
import org.evernet.request.PropertyUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    private final StructureService structureService;

    public Property create(String nodeIdentifier, String structureAddress, PropertyCreationRequest request, String creator) {
        if (!structureService.exists(structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Structure %s not found on node %s", structureAddress, nodeIdentifier));
        }

        if (propertyRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(nodeIdentifier, structureAddress, request.getIdentifier())) {
            throw new ClientException(String.format("Property %s already exists for structure %s on node %s", request.getIdentifier(), structureAddress, nodeIdentifier));
        }

        Property property = Property.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .dataFormat(request.getDataFormat())
                .dataSchema(request.getDataSchema())
                .creator(creator)
                .build();

        return propertyRepository.save(property);
    }

    public List<Property> list(String nodeIdentifier, String structureAddress) {
        return propertyRepository.findByNodeIdentifierAndStructureAddress(nodeIdentifier, structureAddress);
    }

    public Property get(String identifier, String structureAddress, String nodeIdentifier) {
        Property property = propertyRepository.findByIdentifierAndStructureAddressAndNodeIdentifier(identifier, structureAddress, nodeIdentifier);

        if (property == null) {
            throw new NotFoundException(String.format("Property %s not found for structure %s on node %s", identifier, structureAddress, nodeIdentifier));
        }

        return property;
    }

    public Property update(String identifier, PropertyUpdateRequest request, String structureAddress, String nodeIdentifier) {
        Property property = get(identifier, structureAddress, nodeIdentifier);

        if (request.getDataFormat() != null) {
            property.setDataFormat(request.getDataFormat());
        }

        property.setDataSchema(request.getDataSchema());

        if (StringUtils.hasText(request.getDisplayName())) {
            property.setDisplayName(request.getDisplayName());
        }

        property.setDescription(request.getDescription());

        return propertyRepository.save(property);
    }

    public Property delete(String identifier, String structureAddress, String nodeIdentifier) {
        Property property = get(identifier, structureAddress, nodeIdentifier);
        propertyRepository.delete(property);
        return property;
    }
}
