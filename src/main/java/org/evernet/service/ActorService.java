package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.repository.ActorRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    private final NodeService nodeService;
}
