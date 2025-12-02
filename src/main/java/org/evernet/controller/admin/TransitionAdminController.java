package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Transition;
import org.evernet.request.TransitionCreationRequest;
import org.evernet.request.TransitionUpdateRequest;
import org.evernet.service.TransitionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class TransitionAdminController extends AuthenticatedAdminController {

    private final TransitionService transitionService;

    @PostMapping("/transitions")
    public Transition create(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody TransitionCreationRequest request) {
        return transitionService.create(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/transitions")
    public List<Transition> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return transitionService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/transitions/{identifier}")
    public Transition get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return transitionService.get(identifier, structureAddress, nodeIdentifier);
    }

    @PutMapping("/transitions/{identifier}")
    public Transition update(@PathVariable String nodeIdentifier, @PathVariable String identifier, @RequestParam String structureAddress, @Valid @RequestBody TransitionUpdateRequest request) {
        return transitionService.update(
                identifier,
                request,
                structureAddress,
                nodeIdentifier
        );
    }

    @DeleteMapping("/transitions/{identifier}")
    public Transition delete(@PathVariable String nodeIdentifier, @PathVariable String identifier, @RequestParam String structureAddress) {
        return transitionService.delete(identifier, structureAddress, nodeIdentifier);
    }
}
