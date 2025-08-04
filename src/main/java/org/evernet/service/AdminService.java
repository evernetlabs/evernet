package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.NotAllowedException;
import org.evernet.model.Admin;
import org.evernet.repository.AdminRepository;
import org.evernet.request.AdminInitRequest;
import org.evernet.util.Password;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final ConfigService configService;

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
}
