package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Node;
import org.evernet.repository.NodeRepository;
import org.evernet.request.NodeCreationRequest;
import org.evernet.request.NodeOpenUpdateRequest;
import org.evernet.request.NodeUpdateRequest;
import org.evernet.util.Ed25519KeyHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    public Node create(NodeCreationRequest request, String creator) throws NoSuchAlgorithmException {
        if (nodeRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException(String.format("Node %s already exists", request.getIdentifier()));
        }

        KeyPair signingKeyPair = Ed25519KeyHelper.generateKeyPair();

        String signingPrivateKey = Ed25519KeyHelper.privateKeyToString(signingKeyPair.getPrivate());
        String signingPublicKey = Ed25519KeyHelper.publicKeyToString(signingKeyPair.getPublic());

        Node node = Node.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .signingPrivateKey(signingPrivateKey)
                .signingPublicKey(signingPublicKey)
                .open(request.getOpen())
                .creator(creator)
                .build();

        return nodeRepository.save(node);
    }

    public List<Node> listAll(Pageable pageable) {
        return nodeRepository.findAll(pageable).getContent();
    }

    public List<Node> listOpen(Pageable pageable) {
        return nodeRepository.findByOpenIsTrue(pageable);
    }

    public Node get(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);

        if (node == null) {
            throw new NotFoundException(String.format("Node %s not found", identifier));
        }

        return node;
    }

    public Node update(String identifier, NodeUpdateRequest request) {
        Node node = get(identifier);

        if (StringUtils.hasText(request.getDisplayName())) {
            node.setDisplayName(request.getDisplayName());
        }

        node.setDescription(request.getDescription());

        return nodeRepository.save(node);
    }

    public Node delete(String identifier) {
        Node node = get(identifier);
        nodeRepository.delete(node);
        return node;
    }

    public Node resetSigningKeys(String identifier) throws NoSuchAlgorithmException {
        Node node = get(identifier);
        KeyPair keyPair = Ed25519KeyHelper.generateKeyPair();
        String signingPrivateKey = Ed25519KeyHelper.privateKeyToString(keyPair.getPrivate());
        String signingPublicKey = Ed25519KeyHelper.publicKeyToString(keyPair.getPublic());
        node.setSigningPrivateKey(signingPrivateKey);
        node.setSigningPublicKey(signingPublicKey);
        return nodeRepository.save(node);
    }

    public Node updateOpen(String identifier, NodeOpenUpdateRequest request) {
        Node node = get(identifier);
        node.setOpen(request.getOpen());
        return nodeRepository.save(node);
    }

    public Boolean exists(String identifier) {
        return nodeRepository.existsByIdentifier(identifier);

    }
}
