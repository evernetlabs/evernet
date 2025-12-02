package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Workflow;
import org.evernet.request.WorkflowCreationRequest;
import org.evernet.request.WorkflowUpdateRequest;
import org.evernet.service.WorkflowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class WorkflowAdminController extends AuthenticatedAdminController {

    private final WorkflowService workflowService;

    @PostMapping("/workflows")
    public Workflow create(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody WorkflowCreationRequest request) {
        return workflowService.create(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/workflows")
    public List<Workflow> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return workflowService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/workflows/{identifier}")
    public Workflow get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return workflowService.get(identifier, structureAddress, nodeIdentifier);
    }

    @PutMapping("/workflows/{identifier}")
    public Workflow update(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier, @Valid @RequestBody WorkflowUpdateRequest request) {
        return workflowService.update(identifier, request, structureAddress, nodeIdentifier);
    }

    @DeleteMapping("/workflows/{identifier}")
    public Workflow delete(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return workflowService.delete(identifier, structureAddress, nodeIdentifier);
    }
}
