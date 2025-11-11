package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.TransitionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitionService {

    private final TransitionRepository transitionRepository;

    private final StructureService structureService;

    // TODO
}
