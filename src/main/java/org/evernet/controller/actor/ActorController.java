package org.evernet.controller.actor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.model.Actor;
import org.evernet.request.ActorPasswordChangeRequest;
import org.evernet.request.ActorUpdateRequest;
import org.evernet.service.ActorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActorController extends AuthenticatedActorController {

    private final ActorService actorService;

    @GetMapping("/actors/current")
    public Actor get() {
        checkLocal();
        return actorService.get(getActorIdentifier(), getActorNodeIdentifier());
    }

    @PutMapping("/actors/current")
    public Actor update(@Valid @RequestBody ActorUpdateRequest request) {
        checkLocal();
        return actorService.update(getActorIdentifier(), request, getActorNodeIdentifier());
    }

    @PutMapping("/actors/current/password")
    public Actor changePassword(@Valid @RequestBody ActorPasswordChangeRequest request) {
        checkLocal();
        return actorService.changePassword(getActorIdentifier(), request, getActorNodeIdentifier());
    }

    @DeleteMapping("/actors/current")
    public Actor delete() {
        checkLocal();
        return actorService.delete(getActorIdentifier(), getActorNodeIdentifier());
    }
}
