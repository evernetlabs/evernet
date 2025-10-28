package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Actor;
import org.evernet.request.ActorAdditionRequest;
import org.evernet.request.ActorUpdateRequest;
import org.evernet.response.ActorPasswordResponse;
import org.evernet.service.ActorService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class ActorAdminController extends AuthenticatedAdminController {

    private final ActorService actorService;

    @PostMapping("/actors")
    public ActorPasswordResponse add(@PathVariable String nodeIdentifier, @Valid @RequestBody ActorAdditionRequest request) {
        return actorService.add(nodeIdentifier, request, getAdminIdentifier());
    }

    @GetMapping("/actors")
    public List<Actor> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return actorService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/actors/{identifier}")
    public Actor get(@PathVariable String nodeIdentifier, @PathVariable String identifier) {
        return actorService.get(identifier, nodeIdentifier);
    }

    @PutMapping("/actors/{identifier}")
    public Actor update(@PathVariable String nodeIdentifier, @PathVariable String identifier, @Valid @RequestBody ActorUpdateRequest request) {
        return actorService.update(identifier, request, nodeIdentifier);
    }

    @DeleteMapping("/actors/{identifier}")
    public Actor delete(@PathVariable String identifier, @PathVariable String nodeIdentifier) {
        return actorService.delete(identifier, nodeIdentifier);
    }

    @PutMapping("/actors/{identifier}/password")
    public ActorPasswordResponse resetPassword(@PathVariable String nodeIdentifier, @PathVariable String identifier) {
        return actorService.resetPassword(identifier, nodeIdentifier);
    }
}
