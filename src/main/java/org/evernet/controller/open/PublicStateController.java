package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.State;
import org.evernet.service.StateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PublicStateController {

    private final StateService stateService;

    @GetMapping("/states")
    public List<State> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return stateService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/states/{identifier}")
    public State get(@PathVariable String nodeIdentifier, @PathVariable String identifier, @RequestParam String structureAddress) {
        return stateService.get(identifier, structureAddress, nodeIdentifier);
    }
}
