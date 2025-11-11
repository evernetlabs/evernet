package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.WorkflowRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    private final StructureService structureService;

    // TODO
}
