package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.model.Admin;
import xyz.evernet.request.AdminInitRequest;
import xyz.evernet.request.AdminTokenRequest;
import xyz.evernet.response.AdminTokenResponse;
import xyz.evernet.service.AdminService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AdminService adminService;

    @PostMapping("/admins/init")
    public Admin init(@Valid @RequestBody AdminInitRequest request) {
        return adminService.init(request);
    }

    @PostMapping("/admins/token")
    public AdminTokenResponse getToken(@Valid @RequestBody AdminTokenRequest request) {
        return adminService.getToken(request);
    }
}
