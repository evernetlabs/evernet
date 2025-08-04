package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.response.VertexInfoResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VertexService {

    private final ConfigService configService;

    public VertexInfoResponse getInfo() {
        return VertexInfoResponse.builder()
                .displayName(configService.getVertexDisplayName())
                .description(configService.getVertexDescription())
                .endpoint(configService.getVertexEndpoint())
                .build();
    }
}
