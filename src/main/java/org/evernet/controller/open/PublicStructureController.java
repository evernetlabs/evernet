package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Structure;
import org.evernet.service.StructureService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class PublicStructureController {

    private final StructureService structureService;

    @GetMapping("/structures")
    public List<Structure> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return structureService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/structure")
    public Structure get(@RequestParam String address, @PathVariable String nodeIdentifier) {
        return structureService.get(address, nodeIdentifier);
    }
}
