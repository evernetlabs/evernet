package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.TransitionActionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitionActionService {

    private final TransitionActionRepository transitionActionRepository;

    private final TransitionService transitionService;

    // TODO
}
