package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.request.FederationProtocolConfigRequest;
import org.evernet.service.ConfigService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admins/config")
@RequiredArgsConstructor
public class ConfigAdminController {

    private final ConfigService configService;

    @PutMapping("/jwt-signing-key")
    public void setJwtSigningKey() {
        configService.setJwtSigningKey();
    }

    @PutMapping("/federation-protocol")
    public void setFederationProtocol(@Valid @RequestBody FederationProtocolConfigRequest request) {
        if (!request.getFederationProtocol().equals("http") && !request.getFederationProtocol().equals("https")) {
            throw new ClientException("Invalid federation protocol. Allowed values are 'http' or 'https'.");
        }

        configService.setFederationProtocol(request.getFederationProtocol());
    }
}
