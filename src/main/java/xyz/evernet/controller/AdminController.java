package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.auth.AuthenticatedAdminController;
import xyz.evernet.model.Admin;
import xyz.evernet.request.AdminAdditionRequest;
import xyz.evernet.request.AdminPasswordChangeRequest;
import xyz.evernet.response.AdminPasswordResponse;
import xyz.evernet.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AdminController extends AuthenticatedAdminController {

    private final AdminService adminService;

    @GetMapping("/admins/current")
    public Admin get() {
        return adminService.get(getUsername());
    }

    @PutMapping("/admins/current/password")
    public Admin changePassword(@Valid @RequestBody AdminPasswordChangeRequest request) {
        return adminService.changePassword(getUsername(), request);
    }

    @PostMapping("/admins")
    public AdminPasswordResponse add(@Valid @RequestBody AdminAdditionRequest request) {
        return adminService.add(request, getUsername());
    }

    @GetMapping("/admins")
    public List<Admin> list(Pageable pageable) {
        return adminService.list(pageable);
    }

    @GetMapping("/admins/{username}")
    public Admin get(@PathVariable String username) {
        return adminService.get(username);
    }

    @DeleteMapping("/admins/{username}")
    public Admin delete(@PathVariable String username) {
        return adminService.delete(getUsername());
    }

    @PutMapping("/admins/{username}/password")
    public AdminPasswordResponse resetPassword(@PathVariable String username) {
        return adminService.resetPassword(username);
    }
}
