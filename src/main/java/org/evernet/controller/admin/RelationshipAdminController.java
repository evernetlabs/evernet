package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Relationship;
import org.evernet.request.RelationshipCreationRequest;
import org.evernet.service.RelationshipService;
import org.springframework.web.bind.annotation.*;

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
}
