package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.StateRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StateService {

    private final StateRepository stateRepository;

    private final StructureService structureService;
}
