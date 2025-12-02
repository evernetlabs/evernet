package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.WorkflowNodeDependency;
import org.evernet.repository.WorkflowNodeDependencyRepository;
import org.evernet.request.WorkflowNodeDependencyRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowNodeDependencyService {

    private final WorkflowNodeDependencyRepository workflowNodeDependencyRepository;

    private final WorkflowNodeService workflowNodeService;

    public WorkflowNodeDependency add(String workflowNodeIdentifier, WorkflowNodeDependencyRequest request, String workflowIdentifier, String structureAddress, String nodeIdentifier, String creator) {
        if (!workflowNodeService.exists(
                workflowNodeIdentifier,
                workflowIdentifier,
                structureAddress,
                nodeIdentifier
        )) {
            throw new NotFoundException(String.format("Workflow node %s not found for workflow %s on structure %s on node %s", workflowNodeIdentifier, workflowIdentifier, structureAddress, nodeIdentifier));
        }

        if (!workflowNodeService.exists(request.getDependencyWorkflowNodeIdentifier(), workflowIdentifier, structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Workflow node %s not found for workflow %s on structure %s on node %s", request.getDependencyWorkflowNodeIdentifier(), workflowIdentifier, structureAddress, nodeIdentifier));
        }

        if (workflowNodeDependencyRepository.existsByNodeIdentifierAndStructureAddressAndWorkflowIdentifierAndDependentWorkflowNodeIdentifierAndDependencyWorkflowNodeIdentifier(
                nodeIdentifier,
                structureAddress,
                workflowIdentifier,
                workflowNodeIdentifier,
                request.getDependencyWorkflowNodeIdentifier()
        )) {
            throw new ClientException(String.format("Dependency %s already exists for workflow node %s for workflow %s on structure %s on node %s",
                    request.getDependencyWorkflowNodeIdentifier(),
                    workflowNodeIdentifier,
                    workflowIdentifier,
                    structureAddress,
                    nodeIdentifier));
        }

        WorkflowNodeDependency workflowNodeDependency = WorkflowNodeDependency.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .workflowIdentifier(workflowIdentifier)
                .dependentWorkflowNodeIdentifier(workflowNodeIdentifier)
                .dependencyWorkflowNodeIdentifier(request.getDependencyWorkflowNodeIdentifier())
                .creator(creator)
                .build();

        return workflowNodeDependencyRepository.save(workflowNodeDependency);
    }

    public WorkflowNodeDependency delete(String workflowNodeIdentifier, WorkflowNodeDependencyRequest request, String workflowIdentifier, String structureAddress, String nodeIdentifier) {
        WorkflowNodeDependency workflowNodeDependency = workflowNodeDependencyRepository.findByNodeIdentifierAndStructureAddressAndWorkflowIdentifierAndDependentWorkflowNodeIdentifierAndDependencyWorkflowNodeIdentifier(
                nodeIdentifier,
                structureAddress,
                workflowIdentifier,
                workflowNodeIdentifier,
                request.getDependencyWorkflowNodeIdentifier()
        );

        if (workflowNodeDependency == null) {
            throw new NotFoundException(String.format("Dependency %s not found for workflow node %s for workflow %s on structure %s on node %s",
                    request.getDependencyWorkflowNodeIdentifier(),
                    workflowNodeIdentifier,
                    workflowIdentifier,
                    structureAddress,
                    nodeIdentifier));
        }

        return workflowNodeDependencyRepository.save(workflowNodeDependency);
    }

    public List<WorkflowNodeDependency> list(String workflowNodeIdentifier, String workflowIdentifier, String structureAddress, String nodeIdentifier) {
        return workflowNodeDependencyRepository.findByNodeIdentifierAndStructureAddressAndWorkflowIdentifierAndDependentWorkflowNodeIdentifier(
                nodeIdentifier,
                structureAddress,
                workflowIdentifier,
                workflowNodeIdentifier
        );
    }
}
