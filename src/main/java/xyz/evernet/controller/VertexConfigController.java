package xyz.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.model.Vertex;
import xyz.evernet.response.VertexFederationProtocolResponse;
import xyz.evernet.service.VertexConfigService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VertexConfigController {

    private final VertexConfigService vertexConfigService;

    @GetMapping("/vertex")
    public Vertex get() {
        return vertexConfigService.getVertex();
    }

    @GetMapping("/vertex/federation-protocol")
    public VertexFederationProtocolResponse getVertexFederationProtocolResponse() {
        return vertexConfigService.getFederationProtocol();
    }
}
