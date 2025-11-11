package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Node;
import org.evernet.request.NodeCreationRequest;
import org.evernet.request.NodeOpenUpdateRequest;
import org.evernet.request.NodeUpdateRequest;
import org.evernet.service.NodeService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class NodeAdminController extends AuthenticatedAdminController {

    private final NodeService nodeService;

    @PostMapping("/nodes")
    public Node create(@Valid @RequestBody NodeCreationRequest request) throws NoSuchAlgorithmException {
        return nodeService.create(request, getAdminIdentifier());
    }

    @GetMapping("/nodes/all")
    public List<Node> listAll(Pageable pageable) {
        return nodeService.listAll(pageable);
    }

    @GetMapping("/nodes/{identifier}")
    public Node get(@PathVariable String identifier) {
        return nodeService.get(identifier);
    }

    @PutMapping("/nodes/{identifier}")
    public Node update(@PathVariable String identifier, @Valid @RequestBody NodeUpdateRequest request){
        return nodeService.update(identifier, request);
    }

    @DeleteMapping("/nodes/{identifier}")
    public Node delete(@PathVariable String identifier) {
        return nodeService.delete(identifier);
    }

    @PutMapping("/nodes/{identifier}/signing-keys")
    public Node resetSigningKeys(@PathVariable String identifier) throws NoSuchAlgorithmException {
        return nodeService.resetSigningKeys(identifier);
    }

    @PutMapping("/nodes/{identifier}/open")
    public Node updateOpen(@PathVariable String identifier, @Valid @RequestBody NodeOpenUpdateRequest request) {
        return nodeService.updateOpen(identifier, request);
    }
}
