package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.StructureAddress;
import org.evernet.model.Structure;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RemoteStructureService {

    private final RestTemplate restTemplate;

    private final ConfigService configService;

    public Structure get(StructureAddress structureAddress) {
        return restTemplate.getForObject(
                String.format("%s://%s/api/v1/public/nodes/%s/structure",
                        configService.getFederationProtocol(),
                        structureAddress.getNodeAddress().getVertexEndpoint(),
                        structureAddress.getNodeAddress().getIdentifier()
                ),
                Structure.class,
                "address", structureAddress.toString());
    }
}
