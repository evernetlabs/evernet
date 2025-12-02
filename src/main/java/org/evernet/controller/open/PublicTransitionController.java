package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Transition;
import org.evernet.service.TransitionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class PublicTransitionController {

    private final TransitionService transitionService;

    @GetMapping("/transitions")
    public List<Transition> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return transitionService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/transitions/{identifier}")
    public Transition get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return transitionService.get(identifier, structureAddress, nodeIdentifier);
    }
}
