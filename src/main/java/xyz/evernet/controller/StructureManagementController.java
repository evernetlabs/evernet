package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedAdminController;
import xyz.evernet.model.Structure;
import xyz.evernet.request.StructureCreationRequest;
import xyz.evernet.service.StructureService;

@RestController
@RequestMapping("/api/v1/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class StructureManagementController extends AuthenticatedAdminController {

    private final StructureService structureService;

    @PostMapping("/structures")
    public Structure create(@PathVariable String nodeIdentifier, @Valid @RequestBody StructureCreationRequest request) {
        return structureService.create(nodeIdentifier, request, getAdminUsername());
    }

    @DeleteMapping("/structure")
    public Structure delete(@PathVariable String nodeIdentifier, @RequestParam String address) {
        return structureService.delete(nodeIdentifier, address);
    }
}
