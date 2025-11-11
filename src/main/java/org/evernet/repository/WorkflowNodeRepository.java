package org.evernet.repository;

import org.evernet.model.WorkflowNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowNodeRepository extends JpaRepository<WorkflowNode, String> {

}
