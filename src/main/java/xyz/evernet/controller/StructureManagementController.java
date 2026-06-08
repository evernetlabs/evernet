package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedAdminController;
import xyz.evernet.model.Structure;
import xyz.evernet.request.StructureCreationRequest;
import xyz.evernet.service.StructureService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StructureManagementController extends AuthenticatedAdminController {

    private final StructureService structureService;

    @PostMapping("/structures")
    public Structure create(@Valid @RequestBody StructureCreationRequest request) {
        return structureService.create(request, getUsername());
    }

    @DeleteMapping("/structures/{identifier}")
    public Structure delete(@PathVariable String identifier) {
        return structureService.delete(identifier);
    }
}
