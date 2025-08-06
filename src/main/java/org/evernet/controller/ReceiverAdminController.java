package org.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Receiver;
import org.evernet.service.ReceiverService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class ReceiverAdminController extends AuthenticatedAdminController {

    private final ReceiverService receiverService;

    @PostMapping("/receiver")
    public Receiver create(@PathVariable String nodeIdentifier, @RequestParam String actorAddress) {
        return receiverService.create(nodeIdentifier, actorAddress, getAdminIdentifier());
    }

    @GetMapping("/receivers")
    public List<Receiver> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return receiverService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/receiver")
    public Receiver get(@PathVariable String nodeIdentifier, @RequestParam String actorAddress) {
        return receiverService.get(nodeIdentifier, actorAddress);
    }

    @DeleteMapping("/receiver")
    public Receiver delete(@PathVariable String nodeIdentifier, @RequestParam String actorAddress) {
        return receiverService.delete(nodeIdentifier, actorAddress);
    }
}
