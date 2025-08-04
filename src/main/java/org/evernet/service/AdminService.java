package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdmin;
import org.evernet.auth.Jwt;
import org.evernet.exception.AuthenticationException;
import org.evernet.exception.NotAllowedException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Admin;
import org.evernet.repository.AdminRepository;
import org.evernet.request.AdminInitRequest;
import org.evernet.request.AdminPasswordChangeRequest;
import org.evernet.request.AdminTokenRequest;
import org.evernet.response.AdminTokenResponse;
import org.evernet.util.Password;
import org.springframework.stereotype.Service;

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
                .build();

        admin = adminRepository.save(admin);
        configService.init(request.getVertexEndpoint(), request.getVertexDisplayName(), request.getVertexDescription());
        return admin;
    }

    public AdminTokenResponse getToken(AdminTokenRequest request) {
        Admin admin = adminRepository.findByIdentifier(request.getIdentifier());

        if (admin == null || !Password.verify(request.getPassword(), admin.getPassword())) {
            throw new AuthenticationException();
        }

        String token = jwt.getAdminToken(AuthenticatedAdmin.builder()
                        .identifier(admin.getIdentifier())
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
}
