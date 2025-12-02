package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Inheritance;
import org.evernet.service.InheritanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/public/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PublicInheritanceController {

    private final InheritanceService inheritanceService;

    @GetMapping("/inheritances")
    public List<Inheritance> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return inheritanceService.list(nodeIdentifier, structureAddress);
    }
}
