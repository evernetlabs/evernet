package org.evernet.controller;

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
        return actorService.get(getActorNodeIdentifier(), getActorIdentifier());
    }

    @PutMapping("/actors/current")
    public Actor update(@Valid @RequestBody ActorUpdateRequest request) {
        checkLocal();
        return actorService.update(getActorNodeIdentifier(), getActorIdentifier(), request);
    }

    @PutMapping("/actors/current/password")
    public Actor changePassword(@Valid @RequestBody ActorPasswordChangeRequest request) {
        checkLocal();
        return actorService.changePassword(getActorNodeIdentifier(), getActorIdentifier(), request);
    }

    @DeleteMapping("/actors/current")
    public Actor delete() {
        checkLocal();
        return actorService.delete(getActorNodeIdentifier(), getActorIdentifier());
    }
}
