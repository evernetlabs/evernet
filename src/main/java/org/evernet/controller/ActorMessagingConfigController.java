package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.model.ActorMessagingConfig;
import org.evernet.request.ActorMessagingConfigDeletionRequest;
import org.evernet.request.ActorMessagingConfigRequest;
import org.evernet.service.ActorMessagingConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class ActorMessagingConfigController extends AuthenticatedActorController {

    private final ActorMessagingConfigService actorMessagingConfigService;

    @PutMapping("/configs")
    public ActorMessagingConfig set(@Valid @RequestBody ActorMessagingConfigRequest request) {
        checkLocal();
        return actorMessagingConfigService.set(getActorNodeIdentifier(), getActorIdentifier(), request, null);
    }

    @GetMapping("/configs")
    public List<ActorMessagingConfig> list() {
        checkLocal();
        return actorMessagingConfigService.list(getActorNodeIdentifier(), getActorIdentifier());
    }

    @DeleteMapping("/configs")
    public ActorMessagingConfig delete(@Valid @RequestBody ActorMessagingConfigDeletionRequest request) {
        checkLocal();
        return actorMessagingConfigService.delete(getActorNodeIdentifier(), getActorIdentifier(), request);
    }
}
