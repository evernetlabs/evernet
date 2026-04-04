package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.evernet.bean.NodeAddress;
import xyz.evernet.bean.StructureAddress;
import xyz.evernet.embedded.Relationship;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Structure;
import xyz.evernet.repository.StructureRepository;
import xyz.evernet.request.StructureCreationRequest;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StructureService {

    private final StructureRepository structureRepository;

    private final NodeService nodeService;

    private final VertexConfigService vertexConfigService;

    private final RemoteNodeService remoteNodeService;

    public Structure create(String nodeIdentifier, StructureCreationRequest request, String creator) {
        if (!nodeService.exists(nodeIdentifier)) {
            throw new NotFoundException(String.format("Node %s not found", nodeIdentifier));
        }

        String vertexEndpoint = vertexConfigService.getVertexEndpoint();
        StructureAddress structureAddress = StructureAddress.builder()
                .identifier(request.getIdentifier())
                .nodeAddress(NodeAddress.builder()
                        .identifier(nodeIdentifier)
                        .vertexEndpoint(vertexEndpoint)
                        .build())
                .build();

        String structureAddressString = structureAddress.toString();

        if (structureRepository.existsByNodeIdentifierAndAddress(nodeIdentifier, structureAddressString)) {
            throw new ClientException(String.format("Structure %s already exists on node %s", structureAddressString, nodeIdentifier));
        }

        if (!CollectionUtils.isEmpty(request.getRelationships())) {
            Set<String> relatedStructureAddresses = new HashSet<>();
            for (Relationship relationship : request.getRelationships().values()) {
                relatedStructureAddresses.add(relationship.getStructureAddress());
            }

            cloneRelatedStructures(relatedStructureAddresses, nodeIdentifier);
        }

        Structure structure = Structure.builder()
                .nodeIdentifier(nodeIdentifier)
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .address(structureAddressString)
                .properties(request.getProperties())
                .events(request.getEvents())
                .relationships(request.getRelationships())
                .functions(request.getFunctions())
                .creator(creator)
                .build();

        return structureRepository.save(structure);
    }

    public List<Structure> list(String nodeIdentifier, Pageable pageable) {
        return structureRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public Structure get(String nodeIdentifier, String structureAddress) {
        Structure structure = structureRepository.findByNodeIdentifierAndAddress(nodeIdentifier, structureAddress);

        if (structure == null) {
            throw new NotFoundException(String.format("Structure %s not found on node %s", structureAddress, nodeIdentifier));
        }

        return structure;
    }

    public Structure delete(String nodeIdentifier, String structureAddress) {
        Structure structure = get(nodeIdentifier, structureAddress);
        structureRepository.delete(structure);
        return structure;
    }

    public List<Structure> get(Collection<String> structureAddresses, String nodeIdentifier) {
        return structureRepository.findByNodeIdentifierAndAddressIn(nodeIdentifier, structureAddresses);
    }

    private void cloneRelatedStructures(Set<String> structureAddresses, String nodeIdentifier) {
        List<Structure> existingStructures = get(structureAddresses, nodeIdentifier);

        Set<String> existingStructureAddresses = new HashSet<>();
        for (Structure existingStructure : existingStructures) {
            existingStructureAddresses.add(existingStructure.getAddress());
        }

        structureAddresses.removeAll(existingStructureAddresses);
        if (CollectionUtils.isEmpty(structureAddresses)) {
            return;
        }

        String currentVertexEndpoint = vertexConfigService.getVertexEndpoint();
        NodeAddress currentNodeAddress = NodeAddress.builder().identifier(nodeIdentifier).vertexEndpoint(currentVertexEndpoint).build();

        Set<String> missingLocalStructureAddresses = new HashSet<>();
        Map<String, Set<StructureAddress>> missingStructuresOnCurrentVertex = new HashMap<>();
        Map<String, Map<String, Set<StructureAddress>>> missingStructuresOnAnotherVertex = new HashMap<>();

        for (String structureAddressString : structureAddresses) {
            StructureAddress structureAddress = StructureAddress.fromString(structureAddressString);
            if (currentNodeAddress.equals(structureAddress.getNodeAddress())) {
                missingLocalStructureAddresses.add(structureAddressString);
            } else {
                if (structureAddress.getNodeAddress().getVertexEndpoint().equals(currentVertexEndpoint)) {
                    Set<StructureAddress> missingStructuresOnCurrentVertexNode = missingStructuresOnCurrentVertex.computeIfAbsent(structureAddress.getNodeAddress().getIdentifier(), _ -> new HashSet<>());
                    missingStructuresOnCurrentVertexNode.add(structureAddress);
                } else {
                    Map<String, Set<StructureAddress>> missingStructuresOnAnotherVertexMap = missingStructuresOnAnotherVertex.computeIfAbsent(structureAddress.getNodeAddress().getVertexEndpoint(), _ -> new HashMap<>());
                    Set<StructureAddress> missingStructuresOnAnotherVertexNode = missingStructuresOnAnotherVertexMap.computeIfAbsent(structureAddress.getNodeAddress().getIdentifier(), _ -> new HashSet<>());
                    missingStructuresOnAnotherVertexNode.add(structureAddress);
                }
            }
        }

        if (!CollectionUtils.isEmpty(missingLocalStructureAddresses)) {
            throw new NotFoundException(String.format("Related structures %s not found", String.join(", ", missingLocalStructureAddresses)));
        }

        List<Structure> sourceStructures = new ArrayList<>();
        if (!CollectionUtils.isEmpty(missingStructuresOnCurrentVertex)) {
            for (Map.Entry<String, Set<StructureAddress>> entry : missingStructuresOnCurrentVertex.entrySet()) {
                String sourceNodeIdentifier = entry.getKey();
                Set<StructureAddress> structureAddressesToCopy = entry.getValue();
                Set<String> structureAddressesStringToCopy = structureAddressesToCopy.stream().map(StructureAddress::toString).collect(Collectors.toSet());
                if (!CollectionUtils.isEmpty(structureAddressesStringToCopy)) {
                    sourceStructures.addAll(get(structureAddressesStringToCopy, sourceNodeIdentifier));
                }
            }
        }

        if (!CollectionUtils.isEmpty(missingStructuresOnAnotherVertex)) {
            for (Map.Entry<String, Map<String, Set<StructureAddress>>> entry : missingStructuresOnAnotherVertex.entrySet()) {
                String sourceVertexEndpoint = entry.getKey();
                Map<String, Set<StructureAddress>> structureAddressesToCopyFromVertex = entry.getValue();
                for (Map.Entry<String, Set<StructureAddress>> vertexEntry : structureAddressesToCopyFromVertex.entrySet()) {
                    String sourceNodeIdentifier = vertexEntry.getKey();
                    Set<StructureAddress> structureAddressesToCopy = vertexEntry.getValue();
                    Set<String> structureAddressesStringToCopy = structureAddressesToCopy.stream().map(StructureAddress::toString).collect(Collectors.toSet());
                    if (!CollectionUtils.isEmpty(structureAddressesToCopy)) {
                        sourceStructures.addAll(remoteNodeService.getStructures(sourceVertexEndpoint, sourceNodeIdentifier, structureAddressesStringToCopy));
                    }
                }
            }
        }

        copyAndSave(sourceStructures, nodeIdentifier);
    }


    private void copyAndSave(List<Structure> sourceStructures, String targetNodeIdentifier) {
        List<Structure> targetStructures = new ArrayList<>();

        for (Structure sourceStructure : sourceStructures) {
            targetStructures.add(Structure.builder()
                    .nodeIdentifier(targetNodeIdentifier)
                    .address(sourceStructure.getAddress())
                    .displayName(sourceStructure.getDisplayName())
                    .description(sourceStructure.getDescription())
                    .properties(sourceStructure.getProperties())
                    .events(sourceStructure.getEvents())
                    .relationships(sourceStructure.getRelationships())
                    .functions(sourceStructure.getFunctions())
                    .creator(sourceStructure.getCreator())
                    .build());
        }

        structureRepository.saveAll(targetStructures);
    }
}
