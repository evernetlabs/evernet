package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Event;
import org.evernet.request.EventCreationRequest;
import org.evernet.request.EventUpdateRequest;
import org.evernet.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class EventAdminController extends AuthenticatedAdminController {

    private final EventService eventService;

    @PostMapping("/events")
    public Event create(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody EventCreationRequest request) {
        return eventService.create(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/events")
    public List<Event> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return eventService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/events/{identifier}")
    public Event get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return eventService.get(identifier, structureAddress, nodeIdentifier);
    }

    @PutMapping("/events/{identifier}")
    public Event update(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody EventUpdateRequest request, @PathVariable String identifier) {
        return eventService.update(
                identifier,
                request,
                structureAddress,
                nodeIdentifier
        );
    }

    @DeleteMapping("/events/{identifier}")
    public Event delete(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return eventService.delete(identifier, structureAddress, nodeIdentifier);
    }
}
