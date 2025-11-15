package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.WorkflowNodeDependency;
import org.evernet.request.WorkflowNodeDependencyRequest;
import org.evernet.service.WorkflowNodeDependencyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure/workflows/{workflowIdentifier}/nodes/{workflowNodeIdentifier}")
@RequiredArgsConstructor
public class WorkflowNodeDependencyAdminController extends AuthenticatedAdminController {

    private final WorkflowNodeDependencyService workflowNodeDependencyService;

    @PostMapping("/dependencies")
    public WorkflowNodeDependency add(@PathVariable String nodeIdentifier,
                                      @PathVariable String workflowNodeIdentifier,
                                      @PathVariable String workflowIdentifier,
                                      @RequestParam String structureAddress,
                                      @Valid @RequestBody WorkflowNodeDependencyRequest request) {
        return workflowNodeDependencyService.add(
                workflowNodeIdentifier,
                request,
                workflowIdentifier,
                structureAddress,
                nodeIdentifier,
                getAdminIdentifier()
        );
    }

    @DeleteMapping("/dependencies")
    public WorkflowNodeDependency delete(@PathVariable String nodeIdentifier,
                                         @PathVariable String workflowNodeIdentifier,
                                         @PathVariable String workflowIdentifier,
                                         @RequestParam String structureAddress,
                                         @Valid @RequestBody WorkflowNodeDependencyRequest request) {
        return workflowNodeDependencyService.delete(
                workflowNodeIdentifier,
                request,
                workflowIdentifier,
                structureAddress,
                nodeIdentifier
        );
    }

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
