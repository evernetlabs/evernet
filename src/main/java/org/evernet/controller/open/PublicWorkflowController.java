package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Workflow;
import org.evernet.service.WorkflowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PublicWorkflowController {

    private final WorkflowService workflowService;

    @GetMapping("/workflows")
    public List<Workflow> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return workflowService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/workflows/{identifier}")
    public Workflow get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return workflowService.get(identifier, structureAddress, nodeIdentifier);
    }
}
