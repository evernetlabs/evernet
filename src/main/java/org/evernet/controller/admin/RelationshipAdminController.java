package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Relationship;
import org.evernet.request.RelationshipCreationRequest;
import org.evernet.request.RelationshipUpdateRequest;
import org.evernet.service.RelationshipService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class RelationshipAdminController extends AuthenticatedAdminController {

    private final RelationshipService relationshipService;

    @PostMapping("/relationships")
    public Relationship create(
            @PathVariable String nodeIdentifier,
            @RequestParam String structureAddress,
            @Valid @RequestBody RelationshipCreationRequest request
    ) {
        return relationshipService.create(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/relationships")
    public List<Relationship> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return relationshipService.list(structureAddress, nodeIdentifier);
    }

    @GetMapping("/relationships/{identifier}")
    public Relationship get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return relationshipService.get(identifier, structureAddress, nodeIdentifier);
    }

    @PutMapping("/relationships/{identifier}")
    public Relationship update(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier, @Valid @RequestBody RelationshipUpdateRequest request) {
        return relationshipService.update(identifier, request, structureAddress, nodeIdentifier);
    }

    @DeleteMapping("/relationships/{identifier}")
    public Relationship delete(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return relationshipService.delete(identifier, structureAddress, nodeIdentifier);
    }
}
