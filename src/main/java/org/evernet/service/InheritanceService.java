package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Inheritance;
import org.evernet.model.Structure;
import org.evernet.repository.InheritanceRepository;
import org.evernet.request.InheritanceRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InheritanceService {

    private final InheritanceRepository inheritanceRepository;

    private final StructureService structureService;

    public Inheritance add(String nodeIdentifier, String structureAddress, InheritanceRequest request, String creator) {
        if (!structureService.exists(structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Structure %s not found on node %s", structureAddress, nodeIdentifier));
        }

        Structure inheritedStructure = structureService.copy(request.getInheritedStructureAddress(), nodeIdentifier);

        if (inheritanceRepository.existsByNodeIdentifierAndStructureAddressAndInheritedStructureAddress(
                nodeIdentifier, structureAddress, inheritedStructure.getAddress())) {
            throw new ClientException(String.format("Structure %s already inherits from %s on node %s",
                    structureAddress, inheritedStructure.getAddress(), nodeIdentifier));
        }

        Inheritance inheritance = Inheritance.builder()
                .structureAddress(structureAddress)
                .inheritedStructureAddress(inheritedStructure.getAddress())
                .nodeIdentifier(nodeIdentifier)
                .creator(creator)
                .build();

        return inheritanceRepository.save(inheritance);
    }

    public List<Inheritance> list(String nodeIdentifier, String structureAddress) {
        return inheritanceRepository.findByNodeIdentifierAndStructureAddress(nodeIdentifier, structureAddress);
    }

    public Inheritance delete(String nodeIdentifier, String structureAddress, InheritanceRequest request) {
        Inheritance inheritance = inheritanceRepository.findByNodeIdentifierAndStructureAddressAndInheritedStructureAddress(
                nodeIdentifier, structureAddress, request.getInheritedStructureAddress()
        );

        inheritanceRepository.delete(inheritance);
        return inheritance;
    }
}
