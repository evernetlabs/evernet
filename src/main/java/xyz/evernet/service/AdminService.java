package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.evernet.auth.AuthenticatedAdmin;
import xyz.evernet.auth.Jwt;
import xyz.evernet.exception.AuthenticationException;
import xyz.evernet.exception.NotAllowedException;
import xyz.evernet.model.Admin;
import xyz.evernet.repository.AdminRepository;
import xyz.evernet.request.AdminInitRequest;
import xyz.evernet.request.AdminTokenRequest;
import xyz.evernet.response.AdminTokenResponse;
import xyz.evernet.util.Ed25519KeyHelper;
import xyz.evernet.util.Password;
import xyz.evernet.util.Random;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

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
}
