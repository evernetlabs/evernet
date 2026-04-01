package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.auth.AuthenticatedAdminController;
import xyz.evernet.request.VertexDescriptionUpdateRequest;
import xyz.evernet.request.VertexDisplayNameUpdateRequest;
import xyz.evernet.request.VertexEndpointUpdateRequest;
import xyz.evernet.request.VertexFederationProtocolUpdateRequest;
import xyz.evernet.response.SuccessResponse;
import xyz.evernet.service.VertexConfigService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VertexConfigManagementController extends AuthenticatedAdminController {

    private final VertexConfigService vertexConfigService;

    @PutMapping("/vertex/endpoint")
    public SuccessResponse setVertexEndpoint(@Valid @RequestBody VertexEndpointUpdateRequest request) {
        vertexConfigService.setVertexEndpoint(request.getEndpoint());
        return SuccessResponse.withMessage("Vertex endpoint updated successfully");
    }

    @PutMapping("/vertex/display-name")
    public SuccessResponse setVertexDisplayName(@Valid @RequestBody VertexDisplayNameUpdateRequest request) {
        vertexConfigService.setVertexDisplayName(request.getDisplayName());
        return SuccessResponse.withMessage("Vertex display name updated successfully");
    }

    @PutMapping("/vertex/description")
    public SuccessResponse setVertexDescription(@Valid @RequestBody VertexDescriptionUpdateRequest request) {
        vertexConfigService.setVertexDescription(request.getDescription());
        return SuccessResponse.withMessage("Vertex description updated successfully");
    }

    @PutMapping("/vertex/jwt-signing-key")
    public SuccessResponse resetJwtSigningKey() {
        vertexConfigService.resetJwtSigningKey();
        return SuccessResponse.withMessage("JWT Signing key reset successfully");
    }

    @PutMapping("/vertex/federation-protocol")
    public SuccessResponse setVertexFederationProtocol(@Valid @RequestBody VertexFederationProtocolUpdateRequest request) {
        vertexConfigService.setFederationProtocol(request.getFederationProtocol());
        return SuccessResponse.withMessage("Federation protocol updated successfully");
    }
}
