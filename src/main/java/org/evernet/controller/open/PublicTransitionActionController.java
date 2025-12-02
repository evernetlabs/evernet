package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.TransitionAction;
import org.evernet.service.TransitionActionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure/transitions/{transitionIdentifier}")
@RequiredArgsConstructor
public class PublicTransitionActionController {

    private final TransitionActionService transitionActionService;

    @GetMapping("/actions")
    public List<TransitionAction> list(@PathVariable String nodeIdentifier, @PathVariable String transitionIdentifier, @RequestParam String structureAddress) {
        return transitionActionService.list(nodeIdentifier, structureAddress, transitionIdentifier);
    }
}
