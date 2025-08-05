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
import org.evernet.request.ActorPasswordChangeRequest;
import org.evernet.request.ActorSignUpRequest;
import org.evernet.request.ActorTokenRequest;
import org.evernet.request.ActorUpdateRequest;
import org.evernet.response.ActorTokenResponse;
import org.evernet.util.Ed25519KeyHelper;
import org.evernet.util.Password;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;

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
}
