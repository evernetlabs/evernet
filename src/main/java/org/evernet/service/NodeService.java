package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.model.Node;
import org.evernet.repository.NodeRepository;
import org.evernet.request.NodeCreationRequest;
import org.evernet.util.Ed25519KeyHelper;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    public Node create(NodeCreationRequest request, String creator) throws NoSuchAlgorithmException {
        if (nodeRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException(String.format("Node %s already exists", request.getIdentifier()));
        }

        KeyPair keyPair = Ed25519KeyHelper.generateKeyPair();

        Node node = Node.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .open(request.getOpen())
                .signingPrivateKey(Ed25519KeyHelper.privateKeyToString(keyPair.getPrivate()))
                .signingPublicKey(Ed25519KeyHelper.publicKeyToString(keyPair.getPublic()))
                .creator(creator)
                .build();

        return nodeRepository.save(node);
    }
}
