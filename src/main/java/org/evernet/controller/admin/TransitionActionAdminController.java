package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.TransitionAction;
import org.evernet.request.TransitionActionRequest;
import org.evernet.service.TransitionActionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure/transitions/{transitionIdentifier}")
@RequiredArgsConstructor
public class TransitionActionAdminController extends AuthenticatedAdminController {

    private final TransitionActionService transitionActionService;

    @PostMapping("/actions")
    public TransitionAction add(@PathVariable String nodeIdentifier, @PathVariable String transitionIdentifier, @RequestParam String structureAddress, @Valid @RequestBody TransitionActionRequest request) {
        return transitionActionService.create(
                nodeIdentifier,
                structureAddress,
                transitionIdentifier,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/actions")
    public List<TransitionAction> list(@PathVariable String nodeIdentifier, @PathVariable String transitionIdentifier, @RequestParam String structureAddress) {
        return transitionActionService.list(nodeIdentifier, structureAddress, transitionIdentifier);
    }

    @DeleteMapping("/actions")
    public TransitionAction delete(@PathVariable String nodeIdentifier, @PathVariable String transitionIdentifier, @RequestParam String structureAddress, @Valid @RequestBody TransitionActionRequest request) {
        return transitionActionService.delete(nodeIdentifier, structureAddress, transitionIdentifier, request);
    }
}
