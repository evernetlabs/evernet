package xyz.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.model.Structure;
import xyz.evernet.service.StructureService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StructureController {

    private final StructureService structureService;

    @GetMapping("/structures")
    public List<Structure> list(Pageable pageable) {
        return structureService.list(pageable);
    }

    @GetMapping("/structures/{identifier}")
    public Structure get(@PathVariable String identifier) {
        return structureService.get(identifier);
    }
}
