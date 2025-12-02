package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.State;
import org.evernet.request.StateCreationRequest;
import org.evernet.request.StateUpdateRequest;
import org.evernet.service.StateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class StateAdminController extends AuthenticatedAdminController {

    private final StateService stateService;

    @PostMapping("/states")
    public State create(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody StateCreationRequest request) {
        return stateService.create(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/states")
    public List<State> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return stateService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/states/{identifier}")
    public State get(@PathVariable String nodeIdentifier, @PathVariable String identifier, @RequestParam String structureAddress) {
        return stateService.get(identifier, structureAddress, nodeIdentifier);
    }

    @PutMapping("/states/{identifier}")
    public State update(@PathVariable String identifier, @PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody StateUpdateRequest request) {
        return stateService.update(
                identifier,
                request,
                structureAddress,
                nodeIdentifier
        );
    }

    @DeleteMapping("/states/{identifier}")
    public State delete(@PathVariable String identifier, @PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return stateService.delete(identifier, structureAddress, nodeIdentifier);
    }
}
