package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.RelationshipChain;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Relationship;
import org.evernet.model.Structure;
import org.evernet.repository.RelationshipRepository;
import org.evernet.request.RelationshipCreationRequest;
import org.evernet.request.RelationshipUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

    public Relationship get(String identifier, String structureAddress, String nodeIdentifier) {
        Relationship relationship = relationshipRepository.findByIdentifierAndFromStructureAddressAndNodeIdentifier(
                identifier, structureAddress, nodeIdentifier
        );

        if (relationship == null) {
            throw new NotFoundException(String.format("Relationship %s not found on structure %s on node %s", identifier, structureAddress, nodeIdentifier));
        }

        return relationship;
    }

    public Relationship update(String identifier, RelationshipUpdateRequest request, String structureAddress, String nodeIdentifier) {
        Relationship relationship = get(identifier, structureAddress, nodeIdentifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            relationship.setDisplayName(request.getDisplayName());
        }

        relationship.setDescription(request.getDescription());
        return relationshipRepository.save(relationship);
    }

    public Relationship delete(String identifier, String structureAddress, String nodeIdentifier) {
        Relationship relationship = get(identifier, structureAddress, nodeIdentifier);
        relationshipRepository.delete(relationship);
        return relationship;
    }

    public Relationship walk(String fromStructureAddress, RelationshipChain relationshipChain, String nodeIdentifier) {
        if (CollectionUtils.isEmpty(relationshipChain.getNodes())) {
            return null;
        }

        Relationship currentRelationship = null;
        for (String relationshipChainNode : relationshipChain.getNodes()) {
            currentRelationship = get(relationshipChainNode, fromStructureAddress, nodeIdentifier);
            fromStructureAddress = currentRelationship.getToStructureAddress();
        }

        return currentRelationship;
    }
}
