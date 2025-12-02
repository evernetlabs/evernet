package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Workflow;
import org.evernet.repository.WorkflowRepository;
import org.evernet.request.WorkflowCreationRequest;
import org.evernet.request.WorkflowUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    private final StructureService structureService;

    public Workflow create(String nodeIdentifier, String structureAddress, WorkflowCreationRequest request, String creator) {

        if (!structureService.exists(structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Structure %s not found on node %s", structureAddress, nodeIdentifier));
        }

        if (workflowRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(nodeIdentifier, structureAddress, request.getIdentifier())) {
            throw new ClientException(String.format("Workflow %s already exists for structure %s not on node %s", request.getIdentifier(), structureAddress, nodeIdentifier));
        }

        Workflow workflow = Workflow.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .creator(creator)
                .build();

        return workflowRepository.save(workflow);
    }

    public List<Workflow> list(String nodeIdentifier, String structureAddress) {
        return workflowRepository.findByNodeIdentifierAndStructureAddress(nodeIdentifier, structureAddress);
    }

    public Workflow get(String identifier, String structureAddress, String nodeIdentifier) {
        Workflow workflow = workflowRepository.findByIdentifierAndStructureAddressAndNodeIdentifier(identifier, structureAddress, nodeIdentifier);

        if (workflow == null) {
            throw new NotFoundException(String.format("Workflow %s not found for structure %s on node %s", identifier, structureAddress, nodeIdentifier));
        }

        return workflow;
    }

    public Workflow update(String identifier, WorkflowUpdateRequest request, String structureAddress, String nodeIdentifier) {
        Workflow workflow = get(identifier, structureAddress, nodeIdentifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            workflow.setDisplayName(request.getDisplayName());
        }

        workflow.setDescription(request.getDescription());
        return workflowRepository.save(workflow);
    }

    public Workflow delete(String identifier, String  structureAddress, String nodeIdentifier) {
        Workflow workflow = get(identifier, structureAddress, nodeIdentifier);
        workflowRepository.delete(workflow);
        return workflow;
    }

    public Boolean exists(String identifier, String structureAddress, String nodeIdentifier) {
        return workflowRepository.existsByNodeIdentifierAndStructureAddressAndIdentifier(nodeIdentifier, structureAddress, identifier);
    }
}
