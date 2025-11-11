package org.evernet.repository;

import org.evernet.model.WorkflowNodeDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowNodeDependencyRepository extends JpaRepository<WorkflowNodeDependency, String> {

}
