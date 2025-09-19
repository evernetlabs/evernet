package org.evernet.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdmin;
import org.evernet.auth.Jwt;
import org.evernet.enums.FederationProtocol;
import org.evernet.exception.AuthenticationException;
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
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final ConfigService configService;
    private final Jwt jwt;

    public Admin init(@Valid @RequestBody AdminInitRequest request) {
        if (adminRepository.count() != 0) {
            throw new NotAllowedException();
        }

        Admin admin = Admin.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .build();

        admin = adminRepository.save(admin);

        configService.init(
                request.getVertexEndpoint(),
                request.getVertexDisplayName(),
                request.getVertexDescription(),
                FederationProtocol.http.toString(),
                Random.generateRandomString(128)
        );

        return admin;
    }

    public AdminTokenResponse getToken(AdminTokenRequest request) {
        Admin admin = adminRepository.findByIdentifier(request.getIdentifier());

        if (admin == null || !Password.verify(request.getPassword(), admin.getPassword())) {
            throw new AuthenticationException();
        }

        String token = jwt.getAdminToken(AuthenticatedAdmin.builder()
                .identifier(request.getIdentifier())
                .build());

        return AdminTokenResponse.builder().token(token).build();
    }

    public Admin get(String identifier) {
        Admin admin = adminRepository.findByIdentifier(identifier);

        if (null == admin) {
            throw new NotFoundException("Admin not found");
        }

        return admin;
    }

    public Admin changePassword(String identifier, AdminPasswordChangeRequest request) {
        Admin admin = get(identifier);
        admin.setPassword(Password.hash(request.getPassword()));
        return adminRepository.save(admin);
    }

    public AdminPasswordResponse add(AdminAdditionRequest request, String creator) {
        if (adminRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException(String.format("Admin %s already exists", request.getIdentifier()));
        }

        String password = Random.generateRandomString(16);

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
        String password = Random.generateRandomString(16);
        Admin admin = changePassword(identifier, AdminPasswordChangeRequest.builder().password(password).build());
        return AdminPasswordResponse.builder().admin(admin).password(password).build();
    }

    public Admin delete(String identifier) {
        Admin admin = get(identifier);
        adminRepository.delete(admin);
        return admin;
    }
}
