package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.model.User;
import xyz.evernet.request.UserSignUpRequest;
import xyz.evernet.request.UserTokenRequest;
import xyz.evernet.response.UserTokenResponse;
import xyz.evernet.service.UserService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserService userService;

    @PostMapping("/users/signup")
    public User signUp(@Valid @RequestBody UserSignUpRequest request) {
        return userService.signUp(request);
    }

    @PostMapping("/users/token")
    public UserTokenResponse getToken(@Valid @RequestBody UserTokenRequest request) {
        return userService.getToken(request);
    }
}
