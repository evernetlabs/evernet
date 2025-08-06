package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActor;
import org.evernet.auth.Jwt;
import org.evernet.bean.ActorAddress;
import org.evernet.bean.NodeAddress;
import org.evernet.exception.AuthenticationException;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotAllowedException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Actor;
import org.evernet.model.Node;
import org.evernet.repository.ActorRepository;
import org.evernet.request.*;
import org.evernet.response.ActorPasswordResponse;
import org.evernet.response.ActorTokenResponse;
import org.evernet.util.Ed25519KeyHelper;
import org.evernet.util.Password;
import org.evernet.util.Random;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    private final NodeService nodeService;

    private final ConfigService configService;

    private final Jwt jwt;

    public Actor signUp(String nodeIdentifier, ActorSignUpRequest request) {
        Node node = nodeService.get(nodeIdentifier);
        if (!node.getOpen()) {
            throw new NotAllowedException();
        }

        if (actorRepository.existsByIdentifierAndNodeIdentifier(request.getIdentifier(), node.getIdentifier())) {
            throw new ClientException(String.format("Actor %s already exists on node %s", request.getIdentifier(), node.getIdentifier()));
        }

        Actor actor = Actor.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .type(request.getType())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .nodeIdentifier(node.getIdentifier())
                .build();

        return actorRepository.save(actor);
    }

    public ActorTokenResponse getToken(String nodeIdentifier, ActorTokenRequest request) throws Exception {
        Node node = nodeService.get(nodeIdentifier);
        PrivateKey signingPrivateKey = Ed25519KeyHelper.stringToPrivateKey(node.getSigningPrivateKey());

        Actor actor = actorRepository.findByIdentifierAndNodeIdentifier(request.getIdentifier(), node.getIdentifier());
        if (actor == null || !Password.verify(request.getPassword(), actor.getPassword())) {
            throw new AuthenticationException();
        }

        String vertexEndpoint = configService.getVertexEndpoint();
        NodeAddress sourceNodeAddress = NodeAddress.builder().identifier(node.getIdentifier()).vertexEndpoint(vertexEndpoint).build();

        NodeAddress targetNodeAddress;
        if (StringUtils.hasText(request.getTargetNodeAddress())) {
            targetNodeAddress = NodeAddress.fromString(request.getTargetNodeAddress());
        } else {
            targetNodeAddress = sourceNodeAddress;
        }

        String token = jwt.getActorToken(AuthenticatedActor.builder()
                .targetNodeAddress(targetNodeAddress)
                .address(ActorAddress.builder()
                        .identifier(actor.getIdentifier())
                        .nodeAddress(sourceNodeAddress)
                        .build())
                .build(), signingPrivateKey);

        return ActorTokenResponse.builder().token(token).build();
    }

    public Actor get(String nodeIdentifier, String identifier) {
        Actor actor = actorRepository.findByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);

        if (actor == null) {
            throw new NotFoundException(String.format("Actor %s not found on node %s", identifier, nodeIdentifier));
        }

        return actor;
    }

    public Actor update(String nodeIdentifier, String identifier, ActorUpdateRequest request) {
        Actor actor = get(nodeIdentifier, identifier);

        actor.setDescription(request.getDescription());

        if (StringUtils.hasText(request.getDisplayName())) {
            actor.setDisplayName(request.getDisplayName());
        }

        if (StringUtils.hasText(request.getType())) {
            actor.setType(request.getType());
        }

        return actorRepository.save(actor);
    }

    public Actor changePassword(String nodeIdentifier, String identifier, ActorPasswordChangeRequest request) {
        Actor actor = get(nodeIdentifier, identifier);
        actor.setPassword(Password.hash(request.getPassword()));
        return actorRepository.save(actor);
    }

    public Actor delete(String nodeIdentifier, String identifier) {
        Actor actor = get(nodeIdentifier, identifier);
        actorRepository.delete(actor);
        return actor;
    }

    public ActorPasswordResponse add(String nodeIdentifier, ActorAdditionRequest request, String creator) {
        if (!nodeService.exists(nodeIdentifier)) {
            throw new NotFoundException(String.format("Node %s does not exists", nodeIdentifier));
        }

        if (actorRepository.existsByIdentifierAndNodeIdentifier(request.getIdentifier(), nodeIdentifier)) {
            throw new ClientException(String.format("Actor %s already exists on node %s", request.getIdentifier(), nodeIdentifier));
        }

        String password = Random.generateRandomString(16);

        Actor actor = Actor.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(password))
                .type(request.getType())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .nodeIdentifier(nodeIdentifier)
                .creator(creator)
                .build();

        actor = actorRepository.save(actor);
        return ActorPasswordResponse.builder().actor(actor).password(password).build();
    }

    public List<Actor> list(String nodeIdentifier, Pageable pageable) {
        return actorRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public ActorPasswordResponse resetPassword(String nodeIdentifier, String identifier) {
        String password = Random.generateRandomString(16);
        Actor actor = get(nodeIdentifier, identifier);
        actor.setPassword(Password.hash(password));
        actor = actorRepository.save(actor);
        return ActorPasswordResponse.builder().actor(actor).password(password).build();
    }

    public Boolean exists(String nodeIdentifier, String identifier) {
        return actorRepository.existsByIdentifierAndNodeIdentifier(identifier, nodeIdentifier);
    }
}
