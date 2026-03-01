package org.evernet.vertex.service;

import lombok.RequiredArgsConstructor;
import org.evernet.vertex.auth.AuthenticatedAdmin;
import org.evernet.vertex.auth.Jwt;
import org.evernet.vertex.enums.FederationProtocol;
import org.evernet.vertex.exception.AuthenticationException;
import org.evernet.vertex.exception.ClientException;
import org.evernet.vertex.exception.NotAllowedException;
import org.evernet.vertex.exception.NotFoundException;
import org.evernet.vertex.model.Admin;
import org.evernet.vertex.repository.AdminRepository;
import org.evernet.vertex.request.AdminAdditionRequest;
import org.evernet.vertex.request.AdminInitRequest;
import org.evernet.vertex.request.AdminPasswordChangeRequest;
import org.evernet.vertex.request.AdminTokenRequest;
import org.evernet.vertex.response.AdminPasswordResponse;
import org.evernet.vertex.response.AdminTokenResponse;
import org.evernet.vertex.util.Password;
import org.evernet.vertex.util.Random;
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
        if (adminRepository.count() > 0) {
            throw new NotAllowedException();
        }

        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .creator(request.getIdentifier())
                .build();

        admin = adminRepository.save(admin);
        configService.init(request.getVertex(), Random.generateRandomString(128), FederationProtocol.HTTP);
        return admin;
    }

    public AdminTokenResponse getToken(AdminTokenRequest request) {
        Admin admin = adminRepository.findByIdentifier(request.getIdentifier());

        if (admin == null || !Password.verify(request.getPassword(), admin.getPassword())) {
            throw new AuthenticationException();
        }

        String token = jwt.getAdminToken(AuthenticatedAdmin.builder().identifier(request.getIdentifier()).build());
        return AdminTokenResponse.builder().token(token).build();
    }

    public Admin get(String identifier) {
        Admin admin = adminRepository.findByIdentifier(identifier);

        if (admin == null) {
            throw new NotFoundException("Admin %s not found".formatted(identifier));
        }

        return admin;
    }

    public Admin changePassword(String identifier, AdminPasswordChangeRequest request) {
        Admin admin = get(identifier);
        admin.setPassword(Password.hash(request.getPassword()));
        return adminRepository.save(admin);
    }

    public AdminPasswordResponse add(AdminAdditionRequest request ,String creator) {
        if (adminRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException("Admin %s already exists".formatted(request.getIdentifier()));
        }

        String password = Random.generateRandomString(16);
        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(password))
                .creator(creator)
                .build();

        admin = adminRepository.save(admin);
        return AdminPasswordResponse.builder().password(password).admin(admin).build();
    }

    public List<Admin> list(Pageable pageable) {
        return adminRepository.findAll(pageable).getContent();
    }

    public AdminPasswordResponse resetPassword(String identifier) {
        Admin admin = get(identifier);
        String newPassword = Random.generateRandomString(16);
        admin.setPassword(Password.hash(newPassword));
        adminRepository.save(admin);
        return AdminPasswordResponse.builder().password(newPassword).admin(admin).build();
    }

    public Admin delete(String identifier) {
        Admin admin = get(identifier);
        adminRepository.delete(admin);
        return admin;
    }
}
