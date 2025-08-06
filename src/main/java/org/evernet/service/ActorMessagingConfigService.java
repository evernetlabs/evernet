package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.NotFoundException;
import org.evernet.model.ActorMessagingConfig;
import org.evernet.repository.ActorMessagingConfigRepository;
import org.evernet.request.ActorMessagingConfigDeletionRequest;
import org.evernet.request.ActorMessagingConfigRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActorMessagingConfigService {

    private final ActorMessagingConfigRepository actorMessagingConfigRepository;

    private final ActorService actorService;

    public ActorMessagingConfig set(String nodeIdentifier, String actorIdentifier, ActorMessagingConfigRequest request, String creator) {
        if (StringUtils.hasText(creator)) {
            if (!actorService.exists(nodeIdentifier, actorIdentifier)) {
                throw new NotFoundException(String.format("Actor %s not found on node %s", actorIdentifier, nodeIdentifier));
            }
        }

        ActorMessagingConfig actorMessagingConfig = actorMessagingConfigRepository.findByNodeIdentifierAndActorIdentifierAndConfigTypeAndNodeAddress(
                nodeIdentifier, actorIdentifier, request.getConfigType(), request.getNodeAddress()
        );

        if (actorMessagingConfig != null) {
            actorMessagingConfig.setPriority(request.getPriority());
            return actorMessagingConfigRepository.save(actorMessagingConfig);
        }

        actorMessagingConfig = ActorMessagingConfig.builder()
                .nodeIdentifier(nodeIdentifier)
                .actorIdentifier(actorIdentifier)
                .configType(request.getConfigType())
                .nodeAddress(request.getNodeAddress())
                .priority(request.getPriority())
                .creator(creator)
                .build();

        return actorMessagingConfigRepository.save(actorMessagingConfig);
    }

    public List<ActorMessagingConfig> list(String nodeIdentifier, String actorIdentifier) {
        return actorMessagingConfigRepository.findByNodeIdentifierAndActorIdentifier(nodeIdentifier, actorIdentifier);
    }

    public ActorMessagingConfig delete(String nodeIdentifier, String actorIdentifier, ActorMessagingConfigDeletionRequest request) {
        ActorMessagingConfig actorMessagingConfig = actorMessagingConfigRepository.findByNodeIdentifierAndActorIdentifierAndConfigTypeAndNodeAddress(
                nodeIdentifier, actorIdentifier, request.getConfigType(), request.getNodeAddress()
        );

        if (actorMessagingConfig == null) {
            throw new NotFoundException(String.format("Messaging config is not found for actor %s on node %s", actorIdentifier, nodeIdentifier));
        }

        actorMessagingConfigRepository.delete(actorMessagingConfig);
        return actorMessagingConfig;
    }
}
