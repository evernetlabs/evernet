package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import xyz.evernet.bean.StructureAddress;
import xyz.evernet.model.RemoteStructure;
import xyz.evernet.model.Structure;
import xyz.evernet.repository.RemoteStructureRepository;

@Service
@RequiredArgsConstructor
public class RemoteStructureCacheService {

    private final RemoteStructureRepository remoteStructureRepository;

    private final RestTemplate restTemplate;

    private final ConfigReaderService configReaderService;

    public Structure get(StructureAddress structureAddress) {

        RemoteStructure remoteStructure = remoteStructureRepository.findByAddress(structureAddress.toString());

        if (remoteStructure != null) {
            return remoteStructure.getData();
        }

        String url = "%s://%s/api/v1/structures/%s".formatted(
                configReaderService.getFederationProtocol(),
                structureAddress.getVertexEndpoint(),
                structureAddress.getIdentifier()
        );

        Structure structure = restTemplate.getForObject(url, Structure.class);

        remoteStructure = RemoteStructure.builder()
                .address(structureAddress.toString())
                .data(structure)
                .build();

        remoteStructureRepository.save(remoteStructure);

        return structure;
    }
}
