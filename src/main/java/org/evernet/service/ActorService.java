package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActor;
import org.evernet.auth.Jwt;
import org.evernet.bean.ActorAddress;
import org.evernet.bean.NodeAddress;
import org.evernet.exception.AuthenticationException;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotAllowedException;
import org.evernet.model.Actor;
import org.evernet.model.Node;
import org.evernet.repository.ActorRepository;
import org.evernet.request.ActorSignUpRequest;
import org.evernet.request.ActorTokenRequest;
import org.evernet.response.ActorTokenResponse;
import org.evernet.util.Ed25519KeyHelper;
import org.evernet.util.Password;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

        if (actorRepository.existsByIdentifierAndNodeIdentifier(request.getIdentifier(), nodeIdentifier)) {
            throw new ClientException(String.format("Actor with identifier %s already exists on node %s", request.getIdentifier(), nodeIdentifier));
        }

        Actor actor = Actor.builder()
                .identifier(request.getIdentifier())
                .password(Password.hash(request.getPassword()))
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .type(request.getType())
                .nodeIdentifier(nodeIdentifier)
                .build();

        return actorRepository.save(actor);
    }

    public ActorTokenResponse getToken(String nodeIdentifier, ActorTokenRequest request) throws Exception {
        Node node = nodeService.get(nodeIdentifier);

        Actor actor = actorRepository.findByNodeIdentifierAndIdentifier(nodeIdentifier, request.getIdentifier());

        if (actor == null || !Password.verify(request.getPassword(), actor.getPassword())) {
            throw new AuthenticationException();
        }

        String vertexEndpoint = configService.getVertexEndpoint();

        NodeAddress actorNodeAddress = NodeAddress.builder()
                .identifier(nodeIdentifier)
                .vertexEndpoint(vertexEndpoint)
                .build();

        NodeAddress targetNodeAddress = StringUtils.hasText(request.getTargetNodeAddress())
                ? NodeAddress.fromString(request.getTargetNodeAddress())
                : actorNodeAddress;

        String token = jwt.getActorToken(
                AuthenticatedActor.builder()
                        .address(ActorAddress.builder()
                                .identifier(actor.getIdentifier())
                                .nodeAddress(actorNodeAddress)
                                .build())
                        .targetNodeAddress(targetNodeAddress)
                        .build(),
                Ed25519KeyHelper.stringToPrivateKey(node.getSigningPrivateKey()));

        return ActorTokenResponse.builder().token(token).build();
    }
}
