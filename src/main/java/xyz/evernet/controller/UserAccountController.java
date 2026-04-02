package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.model.User;
import xyz.evernet.request.UserSignUpRequest;
import xyz.evernet.request.UserTokenRequest;
import xyz.evernet.response.UserTokenResponse;
import xyz.evernet.service.UserService;

@RestController
@RequestMapping("/api/v1/nodes/{nodeIdentifier}")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserService userService;

    @PostMapping("/users/signup")
    public User signUp(@PathVariable String nodeIdentifier, @Valid @RequestBody UserSignUpRequest request) {
        return userService.signUp(nodeIdentifier, request);
    }

    @PostMapping("/users/token")
    public UserTokenResponse getToken(@PathVariable String nodeIdentifier, @Valid @RequestBody UserTokenRequest request) throws Exception {
        return userService.getToken(nodeIdentifier, request);
    }
}
