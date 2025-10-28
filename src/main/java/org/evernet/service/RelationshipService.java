package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.model.Relationship;
import org.evernet.model.Structure;
import org.evernet.repository.RelationshipRepository;
import org.evernet.request.RelationshipCreationRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;

    private final StructureService structureService;

    public Relationship create(String nodeIdentifier, String structureAddress, RelationshipCreationRequest request, String creator) {
        if (!structureService.exists(structureAddress, nodeIdentifier)) {
            throw new ClientException(String.format("Structure %s not found on node %s", structureAddress, nodeIdentifier));
        }

        if (relationshipRepository.existsByIdentifierAndFromStructureAddressAndNodeIdentifier(request.getIdentifier(), structureAddress, nodeIdentifier)) {
            throw new ClientException(String.format("Relationship %s already exists on structure %s on node %s", request.getIdentifier(), structureAddress, nodeIdentifier));
        }

        Structure toStructure = structureService.copy(request.getToStructureAddress(), nodeIdentifier);

        Relationship relationship = Relationship.builder()
                .identifier(request.getIdentifier())
                .fromStructureAddress(structureAddress)
                .toStructureAddress(toStructure.getAddress())
                .nodeIdentifier(nodeIdentifier)
                .displayName(request.getDisplayName())
                .type(request.getType())
                .description(request.getDescription())
                .creator(creator)
                .build();

        return relationshipRepository.save(relationship);
    }

    public List<Relationship> list(String structureAddress, String nodeIdentifier) {
        return relationshipRepository.findByFromStructureAddressAndNodeIdentifier(structureAddress, nodeIdentifier);
    }
}
