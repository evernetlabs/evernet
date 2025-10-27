package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.NodeAddress;
import org.evernet.model.Node;
import org.evernet.util.Ed25519KeyHelper;
import org.springframework.stereotype.Service;

import java.security.PublicKey;

@Service
@RequiredArgsConstructor
public class NodeKeyService {

    private final NodeService nodeService;

    private final RemoteNodeService remoteNodeService;

    private final ConfigService configService;

    public PublicKey getPublicKey(String keyId) throws Exception {
        NodeAddress nodeAddress = NodeAddress.fromString(keyId);

        Node node;
        if (nodeAddress.getVertexEndpoint().equals(configService.getVertexEndpoint())) {
            node = nodeService.get(nodeAddress.getIdentifier());
        } else {
            node = remoteNodeService.get(nodeAddress.getVertexEndpoint(), nodeAddress.getIdentifier());
        }

        return Ed25519KeyHelper.stringToPublicKey(node.getSigningPublicKey());
    }
}