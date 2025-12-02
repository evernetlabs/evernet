package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.WorkflowNode;
import org.evernet.service.WorkflowNodeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure/workflows/{workflowIdentifier}")
@RequiredArgsConstructor
public class PublicWorkflowNodeController {

    private final WorkflowNodeService workflowNodeService;

    @GetMapping("/nodes")
    public List<WorkflowNode> list(@PathVariable String nodeIdentifier, @PathVariable String workflowIdentifier, @RequestParam String structureAddress) {
        return workflowNodeService.list(nodeIdentifier, structureAddress, workflowIdentifier);
    }

    @GetMapping("/nodes/{identifier}")
    public WorkflowNode get(@PathVariable String nodeIdentifier, @PathVariable String workflowIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return workflowNodeService.get(identifier, workflowIdentifier, structureAddress, nodeIdentifier);
    }
}
