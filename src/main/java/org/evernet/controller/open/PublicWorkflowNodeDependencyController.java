package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.WorkflowNodeDependency;
import org.evernet.service.WorkflowNodeDependencyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure/workflows/{workflowIdentifier}/nodes/{workflowNodeIdentifier}")
@RequiredArgsConstructor
public class PublicWorkflowNodeDependencyController {

    private final WorkflowNodeDependencyService workflowNodeDependencyService;

    @GetMapping("/dependencies")
    public List<WorkflowNodeDependency> list(@PathVariable String nodeIdentifier,
                                             @PathVariable String workflowNodeIdentifier,
                                             @PathVariable String workflowIdentifier,
                                             @RequestParam String structureAddress) {
        return workflowNodeDependencyService.list(
                workflowNodeIdentifier,
                workflowIdentifier,
                structureAddress,
                nodeIdentifier
        );
    }
}
