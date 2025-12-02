package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Relationship;
import org.evernet.service.RelationshipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PublicRelationshipController {

    private final RelationshipService relationshipService;

    @GetMapping("/relationships")
    public List<Relationship> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return relationshipService.list(structureAddress, nodeIdentifier);
    }

    @GetMapping("/relationships/{identifier}")
    public Relationship get(@PathVariable String identifier, @PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return relationshipService.get(identifier, nodeIdentifier, structureAddress);
    }
}
