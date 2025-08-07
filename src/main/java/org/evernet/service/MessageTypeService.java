package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.MessageTypeAddress;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.MessageType;
import org.evernet.repository.MessageTypeRepository;
import org.evernet.request.MessageTypeCreationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageTypeService {

    private final MessageTypeRepository messageTypeRepository;

    private final ConfigService configService;

    private final NodeService nodeService;

    private final RestTemplate restTemplate;

    public MessageType create(String nodeIdentifier, MessageTypeCreationRequest request, String creator) {
        if (StringUtils.hasText(creator)) {
            if (!nodeService.exists(nodeIdentifier)) {
                throw new NotFoundException(String.format("Node %s not found", nodeIdentifier));
            }
        }

        MessageTypeAddress messageTypeAddress = MessageTypeAddress.builder()
                .nodeIdentifier(nodeIdentifier)
                .identifier(request.getIdentifier())
                .vertexEndpoint(configService.getVertexEndpoint())
                .build();

        if (messageTypeRepository.existsByNodeIdentifierAndAddress(nodeIdentifier, messageTypeAddress.toString())) {
            throw new ClientException(String.format("Message type %s already exists on node %s", messageTypeAddress, nodeIdentifier));
        }

        MessageType messageType = MessageType.builder()
                .nodeIdentifier(nodeIdentifier)
                .address(messageTypeAddress.toString())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .jsonSchema(request.getJsonSchema())
                .creator(creator)
                .build();

        return messageTypeRepository.save(messageType);
    }

    public List<MessageType> list(String nodeIdentifier, Pageable pageable) {
        return messageTypeRepository.findByNodeIdentifier(nodeIdentifier, pageable);
    }

    public MessageType get(String nodeIdentifier, String messageTypeAddress) {
        MessageType messageType = messageTypeRepository.findByNodeIdentifierAndAddress(nodeIdentifier, messageTypeAddress);

        if (messageType == null) {
            throw new NotFoundException(String.format("Message type %s not found on node %s", messageTypeAddress, nodeIdentifier));
        }

        return messageType;
    }

    public MessageType delete(String nodeIdentifier, String messageTypeAddress) {
        MessageType messageType = get(nodeIdentifier, messageTypeAddress);
        messageTypeRepository.delete(messageType);
        return messageType;
    }

    public MessageType copy(String nodeIdentifier, String messageTypeAddress, String creator) {
        if (StringUtils.hasText(creator)) {
            if (!nodeService.exists(nodeIdentifier)) {
                throw new NotFoundException(String.format("Node %s not found", nodeIdentifier));
            }
        }

        if (messageTypeRepository.existsByNodeIdentifierAndAddress(nodeIdentifier, messageTypeAddress)) {
            throw new ClientException(String.format("Message type %s already exists on node %s", messageTypeAddress, nodeIdentifier));
        }

        MessageTypeAddress address = MessageTypeAddress.fromString(messageTypeAddress);

        MessageType sourceMessageType;

        if (address.getVertexEndpoint().equals(configService.getVertexEndpoint())) {
            sourceMessageType = messageTypeRepository.findByNodeIdentifierAndAddress(address.getNodeIdentifier(), address.toString());
        } else {
            String baseUrl = String.format("%s://%s/api/v1/nodes/%s/messaging/type",
                    configService.getFederationProtocol(),
                    address.getVertexEndpoint(),
                    address.getNodeIdentifier());

            String url = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("address", address.toString())
                    .toUriString();

            sourceMessageType = restTemplate.getForObject(url, MessageType.class);
        }

        if (sourceMessageType == null) {
            throw new NotFoundException(String.format("Source message type not found for %s", address));
        }

        MessageType messageType = MessageType.builder()
                .nodeIdentifier(nodeIdentifier)
                .address(address.toString())
                .displayName(sourceMessageType.getDisplayName())
                .description(sourceMessageType.getDescription())
                .jsonSchema(sourceMessageType.getJsonSchema())
                .creator(sourceMessageType.getCreator())
                .build();

        return messageTypeRepository.save(messageType);
    }
}
