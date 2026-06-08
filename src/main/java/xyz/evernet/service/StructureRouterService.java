package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.evernet.bean.StructureAddress;
import xyz.evernet.model.Structure;

@Service
@RequiredArgsConstructor
public class StructureRouterService {

    private final StructureService structureService;

    private final RemoteStructureCacheService remoteStructureCacheService;

    private final ConfigReaderService configReaderService;

    public Structure get(String address) {
        StructureAddress structureAddress = StructureAddress.from(address);

        if (structureAddress.getVertexEndpoint().equals(configReaderService.getVertexEndpoint())) {
            return structureService.get(structureAddress.getIdentifier());
        } else {
            return remoteStructureCacheService.get(structureAddress);
        }
    }
}
