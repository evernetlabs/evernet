package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.evernet.bean.NodeAddress;
import xyz.evernet.model.Node;

import java.security.PublicKey;

@Service
@RequiredArgsConstructor
public class NodeKeyService {

    private final NodeService nodeService;

    private final RemoteNodeService remoteNodeService;

    private final VertexConfigService vertexConfigService;

    public PublicKey getPublicKey(String keyId) throws Exception {
        NodeAddress nodeAddress = NodeAddress.fromString(keyId);

        Node node;
        if (nodeAddress.getVertexEndpoint().equals(vertexConfigService.getVertexEndpoint())) {
            node = nodeService.get(nodeAddress.getIdentifier());
        } else {
            node = remoteNodeService.get(nodeAddress.getVertexEndpoint(), nodeAddress.getIdentifier());
        }

        return node.getSigningKey().getPublicKeyObject();
    }
}
