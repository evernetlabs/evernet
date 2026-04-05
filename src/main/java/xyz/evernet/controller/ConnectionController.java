package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedUserController;
import xyz.evernet.model.Connection;
import xyz.evernet.request.ConnectionCreationRequest;
import xyz.evernet.service.ConnectionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConnectionController extends AuthenticatedUserController {

    private final ConnectionService connectionService;

    @PostMapping("/connections")
    public Connection create(@Valid @RequestBody ConnectionCreationRequest request) {
        checkLocal();
        return connectionService.create(
                getTargetNodeIdentifier(),
                getUsername(),
                request
        );
    }

    @GetMapping("/connections")
    public List<Connection> list(Pageable pageable) {
        checkLocal();
        return connectionService.list(getTargetNodeIdentifier(), getUsername(), pageable);
    }

    @GetMapping("/connection")
    public Connection get(@RequestParam String connectionUserAddress) {
        checkLocal();
        return connectionService.get(getTargetNodeIdentifier(), getUsername(), connectionUserAddress);
    }

    @DeleteMapping("/connection")
    public Connection delete(@RequestParam String connectionUserAddress) {
        checkLocal();
        return connectionService.delete(getTargetNodeIdentifier(), getUsername(), connectionUserAddress);
    }

    @PostMapping("/connection/lists")
    public Connection addToList(@RequestParam String connectionUserAddress, @RequestParam String connectionListName) {
        checkLocal();
        return connectionService.addToList(getTargetNodeIdentifier(), getUsername(), connectionUserAddress, connectionListName);
    }

    @DeleteMapping("/connection/lists")
    public Connection removeFromList(@RequestParam String connectionUserAddress, @RequestParam String connectionListName) {
        checkLocal();
        return connectionService.removeFromList(getTargetNodeIdentifier(), getUsername(), connectionUserAddress, connectionListName);
    }

    @GetMapping("/connection-list/connections")
    public List<Connection> fetchForList(@RequestParam String connectionListName, Pageable pageable) {
        checkLocal();
        return connectionService.fetchForList(getTargetNodeIdentifier(), getUsername(), connectionListName, pageable);
    }
}
