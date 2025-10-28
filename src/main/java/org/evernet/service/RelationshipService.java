package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.RelationshipRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;

    private final StructureService structureService;
}
