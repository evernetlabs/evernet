package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Inheritance;
import org.evernet.request.InheritanceRequest;
import org.evernet.service.InheritanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class InheritanceAdminController extends AuthenticatedAdminController {

    private final InheritanceService inheritanceService;

    @PostMapping("/inheritances")
    public Inheritance add(
            @PathVariable String nodeIdentifier,
            @Valid @RequestBody InheritanceRequest request,
            @RequestParam String structureAddress
    ) {
        return inheritanceService.add(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/inheritances")
    public List<Inheritance> list(
            @PathVariable String nodeIdentifier,
            @RequestParam String structureAddress
    ) {
        return inheritanceService.list(nodeIdentifier, structureAddress);

    }

    @DeleteMapping("/inheritances")
    public Inheritance delete(
            @PathVariable String nodeIdentifier,
            @RequestParam String structureAddress,
            @Valid @RequestBody InheritanceRequest request
    ) {
        return inheritanceService.delete(
                nodeIdentifier,
                structureAddress,
                request
        );
    }
}
