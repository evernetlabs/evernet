package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedUserController;
import xyz.evernet.model.Node;
import xyz.evernet.request.NodeCreationRequest;
import xyz.evernet.request.SetNodePropertyRequest;
import xyz.evernet.request.UnsetNodePropertyRequest;
import xyz.evernet.service.NodeService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeController extends AuthenticatedUserController {

    private final NodeService nodeService;

    @PostMapping("/nodes")
    public Node create(@Valid @RequestBody NodeCreationRequest request) throws Exception {
        return nodeService.create(request, getUsername());
    }

    @DeleteMapping("/nodes")
    public void delete(@RequestParam String address) throws Exception {
        nodeService.delete(address, getUsername());
    }

    @PutMapping("/nodes/properties")
    public void setProperty(@RequestParam String address, @Valid @RequestBody SetNodePropertyRequest request) throws Exception {
        nodeService.setProperty(address, request, getUsername());
    }

    @DeleteMapping("/nodes/properties")
    public void unsetProperty(@RequestParam String address, @Valid @RequestBody UnsetNodePropertyRequest request) throws Exception {
        nodeService.unsetProperty(address, request, getUsername());
    }
}
