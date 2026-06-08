package xyz.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.bean.Vertex;
import xyz.evernet.response.VertexSigningPublicKeyResponse;
import xyz.evernet.service.ConfigReaderService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VertexController {

    private final ConfigReaderService configReaderService;

    @GetMapping("/vertex")
    public Vertex get() {
        return Vertex.builder()
                .endpoint(configReaderService.getVertexEndpoint())
                .displayName(configReaderService.getVertexDisplayName())
                .description(configReaderService.getVertexDescription())
                .build();
    }

    @GetMapping("/configs/signing-public-key")
    public VertexSigningPublicKeyResponse getSigningPublicKey() {
        return VertexSigningPublicKeyResponse
                .builder()
                .signingPublicKey(configReaderService.getSigningPublicKey())
                .build();
    }
}
