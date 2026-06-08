package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedUserController;
import xyz.evernet.model.User;
import xyz.evernet.request.UserPasswordChangeRequest;
import xyz.evernet.request.UserUpdateRequest;
import xyz.evernet.service.UserService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController extends AuthenticatedUserController {

    private final UserService userService;

    @GetMapping("/users/current")
    public User get() {
        return userService.get(getUsername());
    }

    @PutMapping("/users/current")
    public User update(@Valid @RequestBody UserUpdateRequest request) {
        return userService.update(getUsername(), request);
    }

    @PutMapping("/users/current/password")
    public User changePassword(@Valid @RequestBody UserPasswordChangeRequest request) {
        return userService.changePassword(getUsername(), request);
    }

    @DeleteMapping("/users/current")
    public User delete() {
        return userService.delete(getUsername());
    }
}
