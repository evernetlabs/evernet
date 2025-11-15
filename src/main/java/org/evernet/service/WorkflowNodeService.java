package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.RelationshipChain;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.WorkflowNode;
import org.evernet.repository.WorkflowNodeRepository;
import org.evernet.request.WorkflowNodeCreationRequest;
import org.evernet.request.WorkflowNodeUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowNodeService {

    private final WorkflowNodeRepository workflowNodeRepository;

    private final WorkflowService workflowService;

    private final RelationshipService relationshipService;

    private final FunctionService functionService;

    public WorkflowNode add(String nodeIdentifier, String structureAddress, String workflowIdentifier, WorkflowNodeCreationRequest request, String creator) {
        if (!workflowService.exists(workflowIdentifier, structureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Workflow %s not found for structure %s on node %s", workflowIdentifier, structureAddress, nodeIdentifier));
        }

        if (workflowNodeRepository.existsByNodeIdentifierAndStructureAddressAndWorkflowIdentifierAndIdentifier(nodeIdentifier, structureAddress, workflowIdentifier, request.getIdentifier())) {
            throw new ClientException(String.format("Workflow node %s already exists on workflow %s for structure %s on node %s", request.getIdentifier(), workflowIdentifier, structureAddress, nodeIdentifier));
        }

        RelationshipChain functionParentReferenceChain = RelationshipChain.fromString(request.getFunctionParentReferenceChain());

        String functionParentStructureAddress = structureAddress;
        if (!CollectionUtils.isEmpty(functionParentReferenceChain.getNodes())) {
            functionParentStructureAddress = relationshipService.walk(structureAddress, functionParentReferenceChain, nodeIdentifier).getToStructureAddress();
        }

        if (!functionService.exists(request.getFunctionIdentifier(), functionParentStructureAddress, nodeIdentifier)) {
            throw new NotFoundException(String.format("Function %s not found for structure %s on node %s", request.getFunctionIdentifier(), functionParentStructureAddress, nodeIdentifier));
        }

        WorkflowNode workflowNode = WorkflowNode.builder()
                .nodeIdentifier(nodeIdentifier)
                .structureAddress(structureAddress)
                .workflowIdentifier(workflowIdentifier)
                .functionParentReferenceChain(functionParentReferenceChain.toString())
                .functionIdentifier(request.getFunctionIdentifier())
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .creator(creator)
                .build();

        return workflowNodeRepository.save(workflowNode);
    }

    public List<WorkflowNode> list(String nodeIdentifier, String structureAddress, String workflowIdentifier) {
        return workflowNodeRepository.findByNodeIdentifierAndStructureAddressAndWorkflowIdentifier(nodeIdentifier, structureAddress, workflowIdentifier);
    }

    public WorkflowNode get(String identifier, String workflowIdentifier, String structureAddress, String nodeIdentifier) {
        WorkflowNode workflowNode = workflowNodeRepository.findByIdentifierAndWorkflowIdentifierAndStructureAddressAndNodeIdentifier(
                identifier, workflowIdentifier, structureAddress, nodeIdentifier
        );

        if (workflowNode == null) {
            throw new NotFoundException(String.format("Workflow node %s not found for workflow %s on structure %s on node %s", identifier, workflowIdentifier, structureAddress, nodeIdentifier));
        }

        return workflowNode;
    }

    public WorkflowNode update(String identifier, WorkflowNodeUpdateRequest request, String workflowIdentifier, String structureAddress, String nodeIdentifier) {
        WorkflowNode workflowNode = get(identifier, workflowIdentifier, structureAddress, nodeIdentifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            workflowNode.setDisplayName(request.getDisplayName());
        }

        workflowNode.setDescription(request.getDescription());
        return workflowNodeRepository.save(workflowNode);
    }

    public WorkflowNode delete(String identifier, String workflowIdentifier, String structureAddress, String nodeIdentifier) {
        WorkflowNode workflowNode = get(identifier, workflowIdentifier, structureAddress, nodeIdentifier);
        workflowNodeRepository.delete(workflowNode);
        return workflowNode;
    }

    public Boolean exists(String identifier, String workflowIdentifier, String structureAddress, String nodeIdentifier) {
        return workflowNodeRepository.existsByNodeIdentifierAndStructureAddressAndWorkflowIdentifierAndIdentifier(
                nodeIdentifier,
                structureAddress,
                workflowIdentifier,
                identifier
        );
    }
}
