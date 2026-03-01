package org.evernet.vertex.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.vertex.auth.AuthenticatedAdminController;
import org.evernet.vertex.bean.Vertex;
import org.evernet.vertex.model.Config;
import org.evernet.vertex.request.FederationProtocolUpdateRequest;
import org.evernet.vertex.response.SuccessResponse;
import org.evernet.vertex.service.ConfigService;
import org.evernet.vertex.util.Random;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admins/configs")
@RequiredArgsConstructor
public class ConfigController extends AuthenticatedAdminController {

    private final ConfigService configService;

    @PutMapping("/vertex")
    public Vertex setVertex(Vertex vertex) {
        return configService.setVertex(vertex);
    }

    @PutMapping("/federation-protocol")
    public Config setFederationProtocol(@Valid @RequestBody FederationProtocolUpdateRequest request) {
        return configService.setFederationProtocol(request.getProtocol());
    }

    @PutMapping("/jwt-signing-key")
    public SuccessResponse resetJwtSigningKey() {
        configService.resetJwtSigningKey(Random.generateRandomString(128));
        return SuccessResponse.withMessage("JWT signing key reset successfully");
    }
}
