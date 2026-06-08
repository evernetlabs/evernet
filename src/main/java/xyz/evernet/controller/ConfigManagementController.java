package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.auth.AuthenticatedAdminController;
import xyz.evernet.bean.Vertex;
import xyz.evernet.request.FederationProtocolUpdateRequest;
import xyz.evernet.service.ConfigService;
import xyz.evernet.util.Ed25519KeyHelper;
import xyz.evernet.util.Random;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConfigManagementController extends AuthenticatedAdminController {

    private final ConfigService configService;

    @PutMapping("/vertex")
    public void setVertex(@Valid @RequestBody Vertex vertex) {
        configService.setVertex(vertex);
    }

    @PutMapping("/configs/jwt-signing-key")
    public void resetJwtSigningKey() {
        configService.setJwtSigningKey(Random.generateRandomString(128));
    }

    @PutMapping("/configs/signing-keys")
    public void resetSigningKeys() throws NoSuchAlgorithmException {
        KeyPair keyPair = Ed25519KeyHelper.generateKeyPair();
        configService.setSigningKeys(
                Ed25519KeyHelper.privateKeyToString(keyPair.getPrivate()),
                Ed25519KeyHelper.publicKeyToString(keyPair.getPublic())
        );
    }

    @PutMapping("/configs/federation-protocol")
    public void setFederationProtocol(@Valid @RequestBody FederationProtocolUpdateRequest request) {
        configService.setFederationProtocol(request.getFederationProtocol());
    }
}
