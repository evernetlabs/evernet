package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Property;
import org.evernet.request.PropertyCreationRequest;
import org.evernet.request.PropertyUpdateRequest;
import org.evernet.service.PropertyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PropertyAdminController extends AuthenticatedAdminController {

    private final PropertyService propertyService;

    @PostMapping("/properties")
    public Property create(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody PropertyCreationRequest request) {
        return propertyService.create(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/properties")
    public List<Property> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return propertyService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/properties/{identifier}")
    public Property get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return propertyService.get(identifier, structureAddress, nodeIdentifier);
    }

    @PutMapping("/properties/{identifier}")
    public Property update(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody PropertyUpdateRequest request, @PathVariable String identifier) {
        return propertyService.update(
                identifier,
                request,
                structureAddress,
                nodeIdentifier
        );
    }

    @DeleteMapping("/properties/{identifier}")
    public Property delete(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return propertyService.delete(identifier, structureAddress, nodeIdentifier);
    }
}
