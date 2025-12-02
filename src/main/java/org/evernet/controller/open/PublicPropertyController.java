package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Property;
import org.evernet.service.PropertyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PublicPropertyController {

    private final PropertyService propertyService;

    @GetMapping("/properties")
    public List<Property> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return propertyService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/properties/{identifier}")
    public Property get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return propertyService.get(identifier, structureAddress, nodeIdentifier);
    }
}
