package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.model.Structure;
import xyz.evernet.request.StructuresFetchRequest;
import xyz.evernet.response.StructuresFetchResponse;
import xyz.evernet.service.StructureService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class StructureController {

    private final StructureService structureService;

    @GetMapping("/structures")
    public List<Structure> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return structureService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/structure")
    public Structure get(@PathVariable String nodeIdentifier, @RequestParam String address) {
        return structureService.get(nodeIdentifier, address);
    }

    @PostMapping("/structures/fetch")
    public StructuresFetchResponse get(@PathVariable String nodeIdentifier, @Valid @RequestBody StructuresFetchRequest request) {
        return StructuresFetchResponse.builder()
                .structures(structureService.get(request.getAddresses(), nodeIdentifier))
                .build();
    }
}
