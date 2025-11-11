package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.Vertex;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VertexService {

    private final ConfigService configService;

    public Vertex get() {
        return Vertex.builder()
                .endpoint(configService.getVertexEndpoint())
                .displayName(configService.getVertexDisplayName())
                .description(configService.getVertexDescription())
                .build();
    }

    public Vertex set(Vertex vertex) {
        configService.setVertexEndpoint(vertex.getEndpoint());
        configService.setVertexDisplayName(vertex.getDisplayName());
        configService.setVertexDescription(vertex.getDescription());
        return vertex;
    }
}
