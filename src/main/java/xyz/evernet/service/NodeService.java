package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.evernet.embedded.SigningKey;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Node;
import xyz.evernet.repository.NodeRepository;
import xyz.evernet.request.NodeCreationRequest;
import xyz.evernet.request.NodeOpenUpdateRequest;
import xyz.evernet.request.NodeUpdateRequest;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NodeService {

    private final NodeRepository nodeRepository;

    public Node create(NodeCreationRequest request, String creator) throws NoSuchAlgorithmException {
        if (nodeRepository.existsByIdentifier(request.getIdentifier())) {
            throw new ClientException("Node %s already exists".formatted(request.getIdentifier()));
        }

        SigningKey signingKey = SigningKey.generate();
        Node node = Node.builder()
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .open(request.getOpen())
                .signingKey(signingKey)
                .creator(creator)
                .build();

        return nodeRepository.save(node);
    }

    public List<Node> list(Pageable pageable) {
        return nodeRepository.findAll(pageable).getContent();
    }

    public List<Node> listOpen(Pageable pageable) {
        return nodeRepository.findByOpenIsTrue(pageable);
    }

    public Node get(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);

        if (node == null) {
            throw new NotFoundException("Node %s not found".formatted(identifier));
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

    public Node resetSigningKey(String identifier) throws NoSuchAlgorithmException {
        Node node = get(identifier);
        SigningKey signingKey = SigningKey.generate();
        node.setSigningKey(signingKey);
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
