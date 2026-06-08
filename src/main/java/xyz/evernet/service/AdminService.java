package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.evernet.auth.AuthenticatedAdmin;
import xyz.evernet.auth.Jwt;
import xyz.evernet.exception.AuthenticationException;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotAllowedException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Admin;
import xyz.evernet.repository.AdminRepository;
import xyz.evernet.request.AdminAdditionRequest;
import xyz.evernet.request.AdminInitRequest;
import xyz.evernet.request.AdminPasswordChangeRequest;
import xyz.evernet.request.AdminTokenRequest;
import xyz.evernet.response.AdminPasswordResponse;
import xyz.evernet.response.AdminTokenResponse;
import xyz.evernet.util.Ed25519KeyHelper;
import xyz.evernet.util.Password;
import xyz.evernet.util.Random;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final ConfigService configService;
    private final Jwt jwt;

    public Admin init(AdminInitRequest request) throws NoSuchAlgorithmException {
        if (adminRepository.count() != 0) {
            throw new NotAllowedException();
        }

        Admin admin = Admin.builder()
                .username(request.getUsername())
                .password(Password.hash(request.getPassword()))
                .creator(request.getUsername())
                .build();

        admin = adminRepository.save(admin);

        KeyPair keyPair = Ed25519KeyHelper.generateKeyPair();

        configService.init(
                request.getVertex(),
                "http",
                Random.generateRandomString(128),
                Ed25519KeyHelper.privateKeyToString(keyPair.getPrivate()),
                Ed25519KeyHelper.publicKeyToString(keyPair.getPublic())
        );

        return admin;
    }

    public AdminTokenResponse getToken(AdminTokenRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername());

        if (admin == null || !Password.verify(request.getPassword(), admin.getPassword())) {
            throw new AuthenticationException();
        }

        String token = jwt.getAdminToken(AuthenticatedAdmin.builder().username(admin.getUsername()).build());

        return AdminTokenResponse.builder().token(token).build();
    }

    public Admin get(String username) {
        Admin admin = adminRepository.findByUsername(username);

        if (admin == null) {
            throw new NotFoundException("Admin %s not found".formatted(username));
        }

        return admin;
    }

    public Admin changePassword(String username, AdminPasswordChangeRequest request) {
        Admin admin = get(username);
        admin.setPassword(Password.hash(request.getPassword()));
        return adminRepository.save(admin);
    }

    public AdminPasswordResponse add(AdminAdditionRequest request, String creator) {
        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new ClientException("Username %s is already taken".formatted(request.getUsername()));
        }

        String password = Random.generateRandomString(16);

        Admin admin = Admin.builder()
                .username(request.getUsername())
                .password(Password.hash(password))
                .creator(creator)
                .build();

        admin = adminRepository.save(admin);

        return AdminPasswordResponse.builder().admin(admin).password(password).build();
    }

    public List<Admin> list(Pageable pageable) {
        return adminRepository.findAll(pageable).getContent();
    }

    public Admin delete(String username) {
        Admin admin = get(username);
        adminRepository.delete(admin);
        return admin;
    }

    public AdminPasswordResponse resetPassword(String username) {
        Admin admin = get(username);
        String password = Random.generateRandomString(16);
        admin.setPassword(Password.hash(password));
        admin = adminRepository.save(admin);
        return AdminPasswordResponse.builder().admin(admin).password(password).build();
    }
}
