package org.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Transmitter;
import org.evernet.service.TransmitterService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class TransmitterAdminController extends AuthenticatedAdminController {

    private final TransmitterService transmitterService;

    @PostMapping("/transmitter")
    public Transmitter create(@PathVariable String nodeIdentifier, @RequestParam String actorAddress) {
        return transmitterService.create(nodeIdentifier, actorAddress, getAdminIdentifier());
    }

    @GetMapping("/transmitters")
    public List<Transmitter> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return transmitterService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/transmitter")
    public Transmitter get(@PathVariable String nodeIdentifier, @RequestParam String actorAddress) {
        return transmitterService.get(nodeIdentifier, actorAddress);
    }

    @DeleteMapping("/transmitter")
    public Transmitter delete(@PathVariable String nodeIdentifier, @RequestParam String actorAddress) {
        return transmitterService.delete(nodeIdentifier, actorAddress);
    }
}
