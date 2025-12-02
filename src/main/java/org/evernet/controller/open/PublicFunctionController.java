package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Function;
import org.evernet.service.FunctionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PublicFunctionController {

    private final FunctionService functionService;

    @GetMapping("/functions")
    public List<Function> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return functionService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/functions/{identifier}")
    public Function get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return functionService.get(identifier, structureAddress, nodeIdentifier);
    }
}
