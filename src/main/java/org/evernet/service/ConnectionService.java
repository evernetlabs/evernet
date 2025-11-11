package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.ConnectionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    private final StructureService structureService;

    // TODO
}
