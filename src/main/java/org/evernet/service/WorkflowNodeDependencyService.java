package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.WorkflowNodeDependencyRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowNodeDependencyService {

    private final WorkflowNodeDependencyRepository workflowNodeDependencyRepository;

    private final WorkflowNodeService workflowNodeService;

    // TODO
}
