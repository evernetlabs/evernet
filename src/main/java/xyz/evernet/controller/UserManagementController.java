package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedAdminController;
import xyz.evernet.model.User;
import xyz.evernet.request.UserAdditionRequest;
import xyz.evernet.response.UserPasswordResponse;
import xyz.evernet.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class UserManagementController extends AuthenticatedAdminController {

    private final UserService userService;

    @PostMapping("/users")
    public UserPasswordResponse add(@PathVariable String nodeIdentifier, @Valid @RequestBody UserAdditionRequest request) {
        return userService.add(
                nodeIdentifier,
                request,
                getAdminUsername()
        );
    }

    @GetMapping("/users")
    public List<User> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return userService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/users/{username}")
    public User get(@PathVariable String nodeIdentifier, @PathVariable String username) {
        return userService.get(nodeIdentifier, username);
    }

    @GetMapping("/users/{username}/password")
    public UserPasswordResponse resetPassword(@PathVariable String nodeIdentifier, @PathVariable String username) {
        return userService.resetPassword(nodeIdentifier, username);
    }

    @DeleteMapping("/users/{username}")
    public User delete(@PathVariable String nodeIdentifier, @PathVariable String username) {
        return userService.delete(nodeIdentifier, username);
    }
}
