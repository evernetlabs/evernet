package org.evernet.repository;

import org.evernet.model.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowNodeRepository extends JpaRepository<WorkflowNode, String> {

    boolean existsByNodeIdentifierAndStructureAddressAndWorkflowIdentifierAndIdentifier(String nodeIdentifier, String structureAddress, String workflowIdentifier, String identifier);

    List<WorkflowNode> findByNodeIdentifierAndStructureAddressAndWorkflowIdentifier(String nodeIdentifier, String structureAddress, String workflowIdentifier);

    WorkflowNode findByIdentifierAndWorkflowIdentifierAndStructureAddressAndNodeIdentifier(String identifier, String workflowIdentifier, String structureAddress, String nodeIdentifier);
}
