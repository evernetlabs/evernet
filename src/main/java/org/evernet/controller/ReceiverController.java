package org.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.model.Receiver;
import org.evernet.service.ReceiverService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReceiverController extends AuthenticatedActorController {

    private final ReceiverService receiverService;

    @PostMapping("/receiver")
    public Receiver create() {
        return receiverService.create(getTargetNodeIdentifier(), getActorAddress(), null);
    }

    @GetMapping("/receiver")
    public Receiver get() {
        return receiverService.get(getTargetNodeIdentifier(), getActorAddress());
    }

    @DeleteMapping("/receiver")
    public Receiver delete() {
        return receiverService.delete(getTargetNodeIdentifier(), getActorAddress());
    }
}
