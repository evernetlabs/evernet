package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.WorkflowNodeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkflowNodeService {

    private final WorkflowNodeRepository workflowNodeRepository;

    private final WorkflowService workflowService;

    // TODO
}
