package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.Connection;
import org.evernet.request.ConnectionRequest;
import org.evernet.service.ConnectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class ConnectionAdminController extends AuthenticatedAdminController {

    private final ConnectionService connectionService;

    @PostMapping("/connections")
    public Connection create(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody ConnectionRequest request) {
        return connectionService.create(
                nodeIdentifier,
                structureAddress,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/connections")
    public List<Connection> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return connectionService.list(nodeIdentifier, structureAddress);
    }

    @DeleteMapping("/connections")
    public Connection delete(@PathVariable String nodeIdentifier, @RequestParam String structureAddress, @Valid @RequestBody ConnectionRequest request) {
        return connectionService.delete(nodeIdentifier, structureAddress, request);
    }
}
