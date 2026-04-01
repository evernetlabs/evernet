package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedAdminController;
import xyz.evernet.model.Node;
import xyz.evernet.request.NodeCreationRequest;
import xyz.evernet.request.NodeOpenUpdateRequest;
import xyz.evernet.request.NodeUpdateRequest;
import xyz.evernet.service.NodeService;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeManagementController extends AuthenticatedAdminController {

    private final NodeService nodeService;

    @PostMapping("/nodes")
    public Node create(@Valid @RequestBody NodeCreationRequest request) throws NoSuchAlgorithmException {
        return nodeService.create(request, getAdminUsername());
    }

    @GetMapping("/nodes/all")
    public List<Node> listAll(Pageable pageable) {
        return nodeService.list(pageable);
    }

    @PutMapping("/nodes/{identifier}")
    public Node update(@PathVariable String identifier, @Valid @RequestBody NodeUpdateRequest request) {
        return nodeService.update(identifier, request);
    }

    @DeleteMapping("/nodes/{identifier}")
    public Node delete(@PathVariable String identifier) {
        return nodeService.delete(identifier);
    }

    @PutMapping("/nodes/{identifier}/signing-key")
    public Node resetSigningKey(@PathVariable String identifier) throws NoSuchAlgorithmException {
        return nodeService.resetSigningKey(identifier);
    }

    @PutMapping("/nodes/{identifier}/open")
    public Node updateOpenFlag(@PathVariable String identifier, @Valid @RequestBody NodeOpenUpdateRequest request) {
        return nodeService.updateOpen(identifier, request);
    }
}
