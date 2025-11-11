package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Function;
import org.evernet.request.FunctionCreationRequest;
import org.evernet.request.FunctionUpdateRequest;
import org.evernet.service.FunctionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class FunctionAdminController extends AuthenticatedAdminController {

    private final FunctionService functionService;

    @PostMapping("/functions")
    public Function create(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody FunctionCreationRequest request) {
        return functionService.create(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/functions")
    public List<Function> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return functionService.list(nodeIdentifier, structureAddress);
    }

    @GetMapping("/functions/{identifier}")
    public Function get(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return functionService.get(identifier, structureAddress, nodeIdentifier);
    }

    @PutMapping("/functions/{identifier}")
    public Function update(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody FunctionUpdateRequest request, @PathVariable String identifier) {
        return functionService.update(
                identifier,
                request,
                structureAddress,
                nodeIdentifier
        );
    }

    @DeleteMapping("/functions/{identifier}")
    public Function delete(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @PathVariable String identifier) {
        return functionService.delete(identifier, structureAddress, nodeIdentifier);
    }
}
