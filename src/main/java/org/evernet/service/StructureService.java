package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.NodeAddress;
import org.evernet.bean.StructureAddress;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Structure;
import org.evernet.repository.StructureRepository;
import org.evernet.request.StructureCreationRequest;
import org.evernet.request.StructureUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StructureService {

    private final StructureRepository structureRepository;

    private final ConfigService configService;

    private final RemoteStructureService remoteStructureService;

    public Structure create(StructureCreationRequest request, String nodeIdentifier, String creator) {
        StructureAddress structureAddress = StructureAddress.builder()
                .nodeAddress(NodeAddress.builder().vertexEndpoint(configService.getVertexEndpoint()).identifier(nodeIdentifier).build())
                .identifier(request.getIdentifier())
                .build();

        if (structureRepository.existsByNodeIdentifierAndAddress(nodeIdentifier, structureAddress.toString())) {
            throw new ClientException(String.format("Structure %s already exists on node %s", structureAddress, nodeIdentifier));
        }

        Structure structure = Structure.builder()
                .address(structureAddress.toString())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .nodeIdentifier(nodeIdentifier)
                .creator(creator)
                .build();

        return structureRepository.save(structure);
    }

    public List<Structure> list(String nodeIdentifier, Pageable pageable) {
        return structureRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public Structure get(String address, String nodeIdentifier) {
        Structure structure = structureRepository.findByAddressAndNodeIdentifier(address, nodeIdentifier);

        if (structure == null) {
            throw new NotFoundException(String.format("Structure %s not found on node %s", address, nodeIdentifier));
        }

        return structure;
    }

    public Structure update(String address, StructureUpdateRequest request, String nodeIdentifier) {
        Structure structure = get(address, nodeIdentifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            structure.setDisplayName(request.getDisplayName());
        }

        structure.setDescription(request.getDescription());
        return structureRepository.save(structure);
    }

    public Structure delete(String address, String nodeIdentifier) {
        Structure structure = get(address, nodeIdentifier);
        structureRepository.delete(structure);
        return structure;

    }

    public Boolean exists(String address, String nodeIdentifier) {
        return structureRepository.existsByNodeIdentifierAndAddress(nodeIdentifier, address);

    }

    public Structure copy(String address, String nodeIdentifier) {
        Structure structure = structureRepository.findByAddressAndNodeIdentifier(address, nodeIdentifier);

        if (structure != null) {
            return structure;
        }

        StructureAddress sourceStructureAddress = StructureAddress.fromString(address);

        NodeAddress sourceNodeAddress = sourceStructureAddress.getNodeAddress();

        Structure sourceStructure;
        if (sourceNodeAddress.getVertexEndpoint().equals(configService.getVertexEndpoint())) {
            sourceStructure = get(address, sourceNodeAddress.getIdentifier());
        } else {
            sourceStructure = remoteStructureService.get(sourceStructureAddress);
        }

        Structure targetStructure = Structure.builder()
                .address(sourceStructure.getAddress())
                .description(sourceStructure.getDescription())
                .displayName(sourceStructure.getDisplayName())
                .nodeIdentifier(nodeIdentifier)
                .build();

        return structureRepository.save(targetStructure);
    }
}
