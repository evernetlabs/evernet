package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.WorkflowNode;
import org.evernet.request.WorkflowNodeCreationRequest;
import org.evernet.request.WorkflowNodeUpdateRequest;
import org.evernet.service.WorkflowNodeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure/workflows/{workflowIdentifier}")
@RequiredArgsConstructor
public class WorkflowNodeAdminController extends AuthenticatedAdminController {

    private final WorkflowNodeService workflowNodeService;

    @PostMapping("/nodes")
    public WorkflowNode create(@PathVariable String nodeIdentifier, @PathVariable String workflowIdentifier, @RequestParam String structureAddress, @Valid @RequestBody WorkflowNodeCreationRequest request) {
        return workflowNodeService.add(
                nodeIdentifier,
                structureAddress,
                workflowIdentifier,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/nodes")
    public List<WorkflowNode> list(@PathVariable String nodeIdentifier, @PathVariable String workflowIdentifier, @RequestParam String structureAddress) {
        return workflowNodeService.list(nodeIdentifier, structureAddress, workflowIdentifier);
    }

    @GetMapping("/nodes/{identifier}")
    public WorkflowNode get(@PathVariable String nodeIdentifier, @PathVariable String workflowIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return workflowNodeService.get(identifier, workflowIdentifier, structureAddress, nodeIdentifier);
    }

    @PutMapping("/nodes/{identifier}")
    public WorkflowNode update(@PathVariable String nodeIdentifier, @PathVariable String workflowIdentifier, @Valid @RequestBody WorkflowNodeUpdateRequest request, @RequestParam String structureAddress, @PathVariable String identifier) {
        return workflowNodeService.update(
                identifier,
                request,
                workflowIdentifier,
                structureAddress,
                nodeIdentifier
        );
    }

    @DeleteMapping("/nodes/{identifier}")
    public WorkflowNode delete(@PathVariable String nodeIdentifier, @PathVariable String workflowIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return workflowNodeService.delete(
                identifier,
                workflowIdentifier,
                structureAddress,
                nodeIdentifier
        );
    }
}
