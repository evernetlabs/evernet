package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.model.Transmitter;
import org.evernet.service.TransmitterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TransmitterController extends AuthenticatedActorController {

    private final TransmitterService transmitterService;

    @PostMapping("/transmitter")
    public Transmitter create() {
        return transmitterService.create(getTargetNodeIdentifier(), getActorAddress(), null);
    }

    @GetMapping("/transmitter")
    public Transmitter get() {
        return transmitterService.get(getTargetNodeIdentifier(), getActorAddress());
    }

    @DeleteMapping("/transmitter")
    public Transmitter delete() {
        return transmitterService.delete(getTargetNodeIdentifier(), getActorAddress());
    }
}
