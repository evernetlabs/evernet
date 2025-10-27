package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdmin;
import org.evernet.auth.Jwt;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotAllowedException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Admin;
import org.evernet.repository.AdminRepository;
import org.evernet.request.AdminAdditionRequest;
import org.evernet.request.AdminInitRequest;
import org.evernet.request.AdminPasswordChangeRequest;
import org.evernet.request.AdminTokenRequest;
import org.evernet.response.AdminPasswordResponse;
import org.evernet.response.AdminTokenResponse;
import org.evernet.util.Password;
import org.evernet.util.Random;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final ConfigService configService;
    private final Jwt jwt;

    public Admin init(AdminInitRequest request) {
        if (adminRepository.count() != 0) {
            throw new NotAllowedException();
        }

        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .creator(request.getIdentifier())
                .build();

        admin = adminRepository.save(admin);
        configService.init(request.getVertex());
        return admin;
    }

    public AdminTokenResponse getToken(AdminTokenRequest request) {
        Admin admin = adminRepository.findByIdentifier(request.getIdentifier());

        if (admin == null || !Password.verify(request.getPassword(), admin.getPassword())) {
            throw new NotAllowedException();
        }

        String token = jwt.getAdminToken(AuthenticatedAdmin.builder()
                        .identifier(request.getIdentifier())
                .build());

        return AdminTokenResponse.builder().token(token).build();
    }

    public Admin get(String identifier) {
        Admin admin = adminRepository.findByIdentifier(identifier);

        if (admin == null) {
            throw new NotFoundException(String.format("Admin %s not found", identifier));
        }

        return admin;
    }

    public Admin changePassword(String identifier, AdminPasswordChangeRequest request) {
        Admin admin = get(identifier);
        admin.setPassword(Password.hash(request.getPassword()));
        return adminRepository.save(admin);
    }

    public AdminPasswordResponse add(AdminAdditionRequest request, String creator) {
        String password = Random.generateRandomString(16);

        if (adminRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException(String.format("Admin %s already exists", request.getIdentifier()));
        }

        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(password))
                .creator(creator)
                .build();

        admin = adminRepository.save(admin);

        return AdminPasswordResponse.builder().admin(admin).password(password).build();
    }

    public List<Admin> list(Pageable pageable) {
        return adminRepository.findAll(pageable).getContent();
    }

    public AdminPasswordResponse resetPassword(String identifier) {
        Admin admin = get(identifier);
        String password = Random.generateRandomString(16);
        admin.setPassword(Password.hash(password));
        admin = adminRepository.save(admin);
        return AdminPasswordResponse.builder().admin(admin).password(password).build();
    }

    public Admin delete(String identifier) {
        Admin admin = get(identifier);
        adminRepository.delete(admin);
        return admin;
    }
}
