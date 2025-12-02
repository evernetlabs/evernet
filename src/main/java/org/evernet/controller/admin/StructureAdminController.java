package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Structure;
import org.evernet.request.StructureCreationRequest;
import org.evernet.request.StructureUpdateRequest;
import org.evernet.service.StructureService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class StructureAdminController extends AuthenticatedAdminController {

    private final StructureService structureService;

    @PostMapping("/structures")
    public Structure create(@PathVariable String nodeIdentifier, @Valid @RequestBody StructureCreationRequest request) {
        return structureService.create(request, nodeIdentifier, getAdminIdentifier());
    }

    @GetMapping("/structures")
    public List<Structure> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return structureService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/structure")
    public Structure get(@RequestParam String address, @PathVariable String nodeIdentifier) {
        return structureService.get(address, nodeIdentifier);
    }

    @PutMapping("/structure")
    public Structure update(@RequestParam String address, @PathVariable String nodeIdentifier, @Valid @RequestBody StructureUpdateRequest request) {
        return structureService.update(address, request, nodeIdentifier);
    }

    @DeleteMapping("/structure")
    public Structure delete(@RequestParam String address, @PathVariable String nodeIdentifier) {
        return structureService.delete(address, nodeIdentifier);
    }
}
