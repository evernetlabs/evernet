package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Event;
import org.evernet.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<Event> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return eventService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/events/{identifier}")
    public Event get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return eventService.get(identifier, structureAddress, nodeIdentifier);
    }
}
