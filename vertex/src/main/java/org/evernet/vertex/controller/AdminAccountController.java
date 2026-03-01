package org.evernet.vertex.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.vertex.model.Admin;
import org.evernet.vertex.request.AdminInitRequest;
import org.evernet.vertex.request.AdminTokenRequest;
import org.evernet.vertex.response.AdminTokenResponse;
import org.evernet.vertex.service.AdminService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
