package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedUserController;
import xyz.evernet.model.User;
import xyz.evernet.request.UserPasswordChangeRequest;
import xyz.evernet.request.UserUpdateRequest;
import xyz.evernet.response.UserPasswordResponse;
import xyz.evernet.service.UserService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController extends AuthenticatedUserController {

    private final UserService  userService;

    @GetMapping("/users/current")
    public User get() {
        checkLocal();
        return userService.get(getTargetNodeIdentifier(), getUsername());
    }

    @PutMapping("/users/current")
    public User update(@Valid @RequestBody UserUpdateRequest request) {
        checkLocal();
        return userService.update(getTargetNodeIdentifier(), getUsername(), request);
    }

    @DeleteMapping("/users/current")
    public User delete() {
        checkLocal();
        return userService.delete(getTargetNodeIdentifier(), getUsername());
    }

    @PutMapping("/users/current/password")
    public User changePassword(@Valid @RequestBody UserPasswordChangeRequest request) {
        checkLocal();
        return userService.changePassword(getTargetNodeIdentifier(), getUsername(), request);
    }
}
