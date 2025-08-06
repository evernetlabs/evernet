package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.ActorMessagingConfig;
import org.evernet.request.ActorMessagingConfigDeletionRequest;
import org.evernet.request.ActorMessagingConfigRequest;
import org.evernet.service.ActorMessagingConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/actors/{actorIdentifier}/messaging")
@RequiredArgsConstructor
public class ActorMessagingConfigAdminController extends AuthenticatedAdminController {

    private final ActorMessagingConfigService actorMessagingConfigService;

    @PutMapping("/configs")
    public ActorMessagingConfig set(@PathVariable String nodeIdentifier, @PathVariable String actorIdentifier, @PathVariable @RequestBody ActorMessagingConfigRequest request) {
        return actorMessagingConfigService.set(
                nodeIdentifier,
                actorIdentifier,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/configs")
    public List<ActorMessagingConfig> list(@PathVariable String nodeIdentifier, @PathVariable String actorIdentifier) {
        return actorMessagingConfigService.list(nodeIdentifier, actorIdentifier);
    }

    @DeleteMapping("/configs")
    public ActorMessagingConfig delete(@PathVariable String nodeIdentifier, @PathVariable String actorIdentifier, @Valid @RequestBody ActorMessagingConfigDeletionRequest request) {
        return actorMessagingConfigService.delete(nodeIdentifier, actorIdentifier, request);
    }
}
