package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedUserController;
import xyz.evernet.model.ConnectionList;
import xyz.evernet.request.ConnectionListCreationRequest;
import xyz.evernet.request.ConnectionListUpdateRequest;
import xyz.evernet.service.ConnectionListService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConnectionListController extends AuthenticatedUserController {

    private final ConnectionListService connectionListService;

    @PostMapping("/connection-lists")
    public ConnectionList create(@Valid @RequestBody ConnectionListCreationRequest request) {
        checkLocal();
        return connectionListService.create(
                getTargetNodeIdentifier(),
                request,
                getUsername()
        );
    }

    @GetMapping("/connection-lists")
    public List<ConnectionList> list(Pageable pageable) {
        checkLocal();
        return connectionListService.list(getTargetNodeIdentifier(), getUsername(), pageable);
    }

    @GetMapping("/connection-list")
    public ConnectionList get(@RequestParam String name) {
        checkLocal();
        return connectionListService.get(getTargetNodeIdentifier(), getUsername(), name);
    }

    @PutMapping("/connection-list")
    public ConnectionList update(@RequestParam String name, @Valid @RequestBody ConnectionListUpdateRequest request) {
        checkLocal();
        return connectionListService.update(getTargetNodeIdentifier(), getUsername(), name, request);
    }

    @DeleteMapping("/connection-list")
    public ConnectionList delete(@RequestParam String name) {
        checkLocal();
        return connectionListService.delete(getTargetNodeIdentifier(), getUsername(), name);
    }
}
